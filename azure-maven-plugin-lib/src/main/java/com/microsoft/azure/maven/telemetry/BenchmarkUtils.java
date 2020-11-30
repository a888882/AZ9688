/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.maven.telemetry;


import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BenchmarkUtils {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static Map<String, LocalDateTime> events = new LinkedHashMap<>();
    public static Map<String, ArrayList<Status>> requests = new LinkedHashMap<>();
    public static Map<String, ArrayList<Status>> failedRequests = new LinkedHashMap<>();

    public static String getDateTimeNowString() {
        return LocalDateTime.now().format(formatter);
    }

    public static void recordEvents(String event) {
        events.put(event, LocalDateTime.now());
    }


    public static Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request request = chain.request();
                final long start = System.currentTimeMillis();
                Response response = null;
                try {
                    response = chain.proceed(request);
                    return response;
                } catch (IOException e) {
                    throw e;
                } finally {
                    if (response == null) {
                        long end = System.currentTimeMillis();
                        final long timeCost = end - start;
                        final String url = request.url().toString();
                        final String method = request.method();
                        failedRequests.computeIfAbsent(url, param -> new ArrayList<Status>()).add(new Status(method, timeCost, -1));
                    } else {
                        final long timeCost = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
                        final String url = response.request().url().toString();
                        final String method = response.request().method();
                        final int code = response.code();
                        requests.computeIfAbsent(url, param -> new ArrayList<Status>()).add(new Status(method, timeCost, code));
                    }
                }
            }
        };


    }

    public static class Status {
        public String method;
        public Long timeCost;
        public int code;

        public Status(String method, Long timeCost, int code) {
            this.method = method;
            this.timeCost = timeCost;
            this.code = code;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "method='" + method + '\'' +
                    ", timeCost=" + timeCost +
                    ", code=" + code +
                    '}';
        }
    }
}
