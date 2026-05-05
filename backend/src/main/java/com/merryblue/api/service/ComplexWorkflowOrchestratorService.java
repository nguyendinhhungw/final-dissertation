package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A highly generalized state-machine and workflow orchestration engine.
 * Capable of handling multi-stage, multi-actor approval processes 
 * (e.g., Job Posting Approval: HR -> Manager -> Director).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComplexWorkflowOrchestratorService {

    // Mock Database of Workflow Definitions
    // e.g., "JOB_APPROVAL" -> [ "HR_REVIEW", "MANAGER_REVIEW", "FINANCE_REVIEW", "PUBLISHED" ]
    private final Map<String, List<String>> workflowDefinitions = Map.of(
        "JOB_APPROVAL", List.of("DRAFT", "PENDING_HR", "PENDING_MANAGER", "APPROVED", "REJECTED"),
        "EXPENSE_CLAIM", List.of("SUBMITTED", "MANAGER_APPROVAL", "FINANCE_PROCESSING", "PAID")
    );

    /**
     * Attempts to advance a resource through its workflow based on an action taken by a user.
     * Evaluates complex conditions, permissions, and side-effects.
     */
    public WorkflowResult advanceWorkflow(String workflowType, UUID resourceId, String currentState, String action, UUID actorId, String actorRole) {
        log.info("Evaluating workflow advance: {} for resource {}, from state '{}' via action '{}' by role '{}'", 
                 workflowType, resourceId, currentState, action, actorRole);

        List<String> validStates = workflowDefinitions.get(workflowType);
        if (validStates == null) {
            throw new IllegalArgumentException("Unknown workflow type: " + workflowType);
        }

        if (!validStates.contains(currentState)) {
            throw new IllegalStateException("Current state '" + currentState + "' is invalid for workflow " + workflowType);
        }

        // 1. Evaluate State Transition Matrix (Hardcoded for demonstration, usually DB-driven)
        String nextState = determineNextState(workflowType, currentState, action);
        if (nextState == null) {
            return new WorkflowResult(false, currentState, "Action '" + action + "' is not valid from state '" + currentState + "'");
        }

        // 2. Evaluate Actor Permissions (Can this role execute this transition?)
        boolean hasPermission = evaluateRolePermission(workflowType, currentState, action, actorRole);
        if (!hasPermission) {
            return new WorkflowResult(false, currentState, "Role '" + actorRole + "' is not authorized to perform '" + action + "' on state '" + currentState + "'");
        }

        // 3. Execute Pre-transition Hooks (e.g., Validation logic before moving to APPROVED)
        boolean hooksPassed = executePreTransitionHooks(workflowType, resourceId, nextState);
        if (!hooksPassed) {
            return new WorkflowResult(false, currentState, "Pre-transition validation failed. Cannot move to " + nextState);
        }

        // 4. (Virtual) Persist new state to database
        log.info("Transition successful. Resource {} moved from {} to {}", resourceId, currentState, nextState);

        // 5. Execute Post-transition Side Effects (e.g., Send emails, trigger Webhooks)
        executePostTransitionSideEffects(workflowType, resourceId, nextState, actorId);

        return new WorkflowResult(true, nextState, "Successfully transitioned to " + nextState);
    }

    private String determineNextState(String workflowType, String currentState, String action) {
        if ("JOB_APPROVAL".equals(workflowType)) {
            if ("DRAFT".equals(currentState) && "SUBMIT".equals(action)) return "PENDING_HR";
            if ("PENDING_HR".equals(currentState) && "APPROVE".equals(action)) return "PENDING_MANAGER";
            if ("PENDING_HR".equals(currentState) && "REJECT".equals(action)) return "REJECTED";
            if ("PENDING_MANAGER".equals(currentState) && "APPROVE".equals(action)) return "APPROVED";
            if ("PENDING_MANAGER".equals(currentState) && "REJECT".equals(action)) return "REJECTED";
            if ("REJECTED".equals(currentState) && "REVISE".equals(action)) return "DRAFT";
        }
        return null;
    }

    private boolean evaluateRolePermission(String workflowType, String currentState, String action, String role) {
        if ("JOB_APPROVAL".equals(workflowType)) {
            if ("PENDING_HR".equals(currentState) && "HR_ADMIN".equals(role)) return true;
            if ("PENDING_MANAGER".equals(currentState) && "DEPARTMENT_HEAD".equals(role)) return true;
            if ("DRAFT".equals(currentState) && "RECRUITER".equals(role)) return true;
            if ("REJECTED".equals(currentState) && "RECRUITER".equals(role)) return true;
            // Super admin can do anything
            if ("SUPER_ADMIN".equals(role)) return true;
        }
        return false;
    }

    private boolean executePreTransitionHooks(String workflowType, UUID resourceId, String targetState) {
        if ("JOB_APPROVAL".equals(workflowType) && "APPROVED".equals(targetState)) {
            log.debug("Executing Hook: Validating budget allocation for Job {}", resourceId);
            // Mock: 10% chance budget validation fails
            if (Math.random() > 0.9) {
                log.error("Hook Failed: Insufficient budget allocated for this role.");
                return false; 
            }
        }
        return true;
    }

    private void executePostTransitionSideEffects(String workflowType, UUID resourceId, String enteredState, UUID actorId) {
        log.debug("Executing side effects for entering state: {}", enteredState);
        if ("APPROVED".equals(enteredState)) {
            log.info("Side Effect: Auto-publishing Job {} to public portal and notifying subscribers.", resourceId);
        } else if ("REJECTED".equals(enteredState)) {
            log.info("Side Effect: Sending rejection feedback email to original submitter for Job {}.", resourceId);
        }
    }

    public record WorkflowResult(boolean success, String finalState, String message) { }
}
