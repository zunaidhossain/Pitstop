package com.pitstop.app.constants;

import java.util.EnumSet;
import java.util.Set;

public enum BookingStatus {
    STARTED,
    BOOKED,
    ON_THE_WAY,
    WAITING,
    REPAIRING,
    COMPLETED,               // normal end-state (sequential)

    // Extra terminal statuses (can be set from any previous status, and are final)
    REJECTED,
    TIMED_OUT,
    CANCELLED_BY_APPUSER,
    CANCELLED_BY_WORKSHOPUSER,
    INCOMPLETE;

    // Terminal statuses that may be set from any prior state
    private static final Set<BookingStatus> GLOBAL_TERMINAL =
            EnumSet.of(REJECTED, TIMED_OUT, CANCELLED_BY_APPUSER, CANCELLED_BY_WORKSHOPUSER, INCOMPLETE);

    /**
     * Whether this status is terminal (no outgoing transitions allowed).
     * Treat COMPLETED as terminal too (you can adjust if you want different behavior).
     */
    public boolean isTerminal() {
        return this == COMPLETED || GLOBAL_TERMINAL.contains(this);
    }

    /**
     * Validates whether a transition from 'this' -> newStatus is allowed.
     *
     * Rules implemented:
     * 1. Staying in the same status is allowed.
     * 2. If current status is terminal, no transitions allowed.
     * 3. Any current status can transition to any GLOBAL_TERMINAL status.
     * 4. Otherwise, follow strict incremental order: only next ordinal (no jumps forward).
     * 5. No backward transitions.
     */
    public boolean canTransitionTo(BookingStatus newStatus) {
        if (this == newStatus) {
            return true; // no-op transition allowed
        }

        // If we're already in a terminal state, no further transitions allowed
        if (this.isTerminal()) {
            return false;
        }

        // Transitions to the defined global terminal statuses are allowed from any non-terminal state
        if (GLOBAL_TERMINAL.contains(newStatus)) {
            return true;
        }

        // Prevent backward movement
        if (newStatus.ordinal() < this.ordinal()) {
            return false;
        }

        // Allow only immediate next status in the main linear flow
        return newStatus.ordinal() == this.ordinal() + 1;
    }
}
