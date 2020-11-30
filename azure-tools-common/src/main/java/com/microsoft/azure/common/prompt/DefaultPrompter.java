/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.common.prompt;

import com.microsoft.azure.common.utils.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPrompter implements IPrompter {
    private static final String EMPTY_REPLACEMENT = ":";

    // this code is copied from https://stackoverflow.com/questions/13011657/advanced-parsing-of-numeric-ranges-from-string
    // the author of it is: https://stackoverflow.com/users/433790/ridgerunner
    private static final Pattern REGEX_COMMA_SEPARATED_INTEGER_RANGES = Pattern.compile("^[0-9]+(?:-[0-9]+)?(?:,[0-9]+(?:-[0-9]+)?)*$");

    private static final Pattern REGEX_NEXT_INTEGER_RANGE = Pattern.compile("([0-9]+)(?:-([0-9]+))?(?:,|$)", Pattern.COMMENTS);

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public String promoteString(String message, String defaultValue, Function<String, InputValidateResult<String>> verify, boolean isRequired)
            throws IOException {
        final boolean hasDefaultValue = StringUtils.isNotBlank(defaultValue);
        System.out.print(message);
        System.out.flush();
        return loopInput(defaultValue, hasDefaultValue, isRequired, "", message, input -> {
            if (!isRequired && StringUtils.equals(EMPTY_REPLACEMENT, input.trim())) {
                return InputValidateResult.wrap("");
            }
            final InputValidateResult<String> result = verify.apply(input);
            if (result.getErrorMessage() != null) {
                return InputValidateResult.error(result.getErrorMessage());
            } else {
                return InputValidateResult.wrap(result.getObj());
            }
        });
    }

    public Boolean promoteYesNo(String message, Boolean defaultValue, boolean isRequired) throws IOException {
        final boolean hasDefaultValue = defaultValue != null;
        System.out.print(message);
        System.out.flush();

        return loopInput(defaultValue, hasDefaultValue, isRequired, "", message, input -> {
            if (input.equalsIgnoreCase("Y")) {
                return InputValidateResult.wrap(Boolean.TRUE);
            }
            if (input.equalsIgnoreCase("N")) {
                return InputValidateResult.wrap(Boolean.FALSE);
            }
            return InputValidateResult.error(String.format("Invalid input (%s).", input));
        });
    }

    public <T> List<T> promoteMultipleEntities(String header, String promotePrefix, String selectNoneMessage, List<T> entities,
            Function<T, String> getNameFunc, boolean allowEmpty, String enterPromote, List<T> defaultValue) throws IOException {
        final boolean hasDefaultValue = defaultValue != null && defaultValue.size() > 0;
        final List<T> res = new ArrayList<>();

        if (!allowEmpty && entities.size() == 1) {
            return entities;
        }
        printOptionList(header, entities, null, getNameFunc);
        final String example = TextUtils.blue("[1-2,4,6]");
        final String hintMessage = String.format("(input numbers separated by comma, eg: %s, %s %s)", example, TextUtils.blue("ENTER"), enterPromote);
        final String promoteMessage = String.format("%s%s: ", promotePrefix, hintMessage);

        for (;;) {
            System.out.print(promoteMessage);
            System.out.flush();
            final String input = reader.readLine();
            if (StringUtils.isBlank(input)) {
                if (hasDefaultValue) {
                    return defaultValue;
                }
                if (allowEmpty) {
                    return res;
                }
                System.out.println(selectNoneMessage);
            }
            if (isValidIntRangeInput(input)) {
                try {
                    for (final int i : parseIntRanges(input, entities.size())) {
                        res.add(entities.get(i - 1));
                    }
                    if (res.size() > 0 || allowEmpty) {
                        return res;
                    }
                    System.out.print(selectNoneMessage);
                } catch (NumberFormatException ex) {
                    System.out.println(TextUtils.yellow(String.format("The input value('%s') is invalid.", input)));
                }
            } else {
                System.out.println(TextUtils.yellow(String.format("The input value('%s') is invalid.", input)));
            }
            System.out.flush();
        }
    }

    public <T> T promoteSingleEntity(String header, String message, List<T> entities, T defaultEntity, Function<T, String> getNameFunc,
            boolean isRequired) throws IOException {
        final boolean hasDefaultValue = defaultEntity != null;

        printOptionList(header, entities, defaultEntity, getNameFunc);

        final int selectedIndex = entities.indexOf(defaultEntity);

        final String defaultValueMessage = selectedIndex >= 0 ? " (" + TextUtils.blue(Integer.toString(selectedIndex + 1)) + ")" : "";
        final String hintMessage = String.format("[1-%d]%s", entities.size(), defaultValueMessage);
        final String promoteMessage = String.format("%s %s: ", message, hintMessage);
        System.out.print(promoteMessage);
        System.out.flush();

        return loopInput(defaultEntity, hasDefaultValue, isRequired, null, promoteMessage, input -> {
            final InputValidateResult<Integer> selectIndex = validateUserInputAsInteger(input, entities.size(),
                    String.format("You have input a wrong value %s.", TextUtils.red(input)));
            if (selectIndex.getErrorMessage() == null) {
                return InputValidateResult.wrap(entities.get(selectIndex.getObj() - 1));
            }
            return InputValidateResult.error(selectIndex.getErrorMessage());
        });
    }

    private <T> T loopInput(T defaultValue, boolean hasDefaultValue, boolean isRequired, String emptyPromoteMessage, String promoteMessage,
            Function<String, InputValidateResult<T>> handleInput) throws IOException {
        while (true) {
            final String input = reader.readLine();
            if (StringUtils.isBlank(input)) {
                if (hasDefaultValue || !isRequired) {
                    return defaultValue;
                }
                System.out.print(emptyPromoteMessage);
            } else {
                final InputValidateResult<T> res = handleInput.apply(input);
                if (res.getErrorMessage() != null) {
                    System.out.println(TextUtils.yellow(res.getErrorMessage()));
                } else {
                    return res.getObj();
                }
            }
            System.out.print(promoteMessage);
            System.out.flush();
        }
    }

    private InputValidateResult<Integer> validateUserInputAsInteger(String input, int maxValue, String message) {
        if (!NumberUtils.isDigits(input)) {
            return InputValidateResult.error(message);
        }
        try {
            final int value = Integer.parseInt(input);
            if (value >= 1 && value <= maxValue) {
                return InputValidateResult.wrap(value);
            }
        } catch (NumberFormatException ex) {
            // ignore since last statement is error
        }

        return InputValidateResult.error(message);
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            // swallow this error
        }
    }

    private static boolean isValidIntRangeInput(String text) {
        return REGEX_COMMA_SEPARATED_INTEGER_RANGES.matcher(text).matches();
    }

    private static Collection<Integer> parseIntRanges(String text, int maxValue) {
        final Matcher m = REGEX_NEXT_INTEGER_RANGE.matcher(text);
        final Set<Integer> values = new LinkedHashSet<>();
        while (m.find()) {
            final int s1 = Integer.parseInt(m.group(1));

            if (m.group(2) != null) {
                // use maxValue to avoid very large enumeration like 1-2^32
                final int s2 = Math.min(maxValue, Integer.parseInt(m.group(2)));
                for (int i = Math.max(1, s1); i <= s2; i++) {
                    values.add(i);
                }
            } else {
                if (s1 >= 1 && s1 <= maxValue) {
                    values.add(s1);
                }
            }
        }
        return values;
    }

    private static <T> void printOptionList(String message, List<T> entities, T defaultEntity, Function<T, String> getNameFunc) {
        int index = 1;
        System.out.println(message);
        for (final T entity : entities) {
            final String displayLine = String.format("%2d. %s", index++, getNameFunc.apply(entity));
            System.out.println(defaultEntity == entity ? TextUtils.blue(displayLine + "*") : displayLine);
        }

    }
}
