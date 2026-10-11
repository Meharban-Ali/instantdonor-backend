package com.blood_donor.exception; // ✅ Bug Fix 1

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

// ═══════════════════════════════════════════════════════════
// ASYNC EXCEPTION HANDLER
// ═══════════════════════════════════════════════════════════
//
// SOLID:
//   S → Sirf async exception handling ka kaam
//
// ✅ Bug Fix 3: Async exceptions silently nahi jayenge
//
// Logic:
//   @Async method mein exception → normally lost ho jaata
//   Yeh handler catch karta hai → log karta hai
//   Future: retry ya alert add kar sakte hain
//
// System Design:
//   Email fail → log karo → monitoring alert
//   SMS fail → log karo → fallback possible

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class AsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(
            Throwable ex,
            Method method,
            Object... params
    ) {
        // ✅ Method details log karo
        log.error(
                "Async method exception: class={}, method={}, "
                        + "params={}, error={}",
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                Arrays.toString(params),
                ex.getMessage(),
                ex  // Full stack trace
        );

        // ✅ Critical async failure → stronger alert
        if (isCriticalMethod(method)) {
            log.error(
                    "CRITICAL async failure in: {}.{}",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName()
            );
        }
    }

    // ─────────────────────────────────────────────────
    // S: Alag method — critical check
    // ─────────────────────────────────────────────────

    private boolean isCriticalMethod(Method method) {
        // Emergency notification fail → critical
        String methodName = method.getName().toLowerCase();
        return methodName.contains("emergency")
                || methodName.contains("critical")
                || methodName.contains("alert");
    }
}