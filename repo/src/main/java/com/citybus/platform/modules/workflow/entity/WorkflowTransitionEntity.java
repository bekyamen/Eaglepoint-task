package com.citybus.platform.modules.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workflow_transitions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransitionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_state_id", nullable = false)
    private WorkflowEntity workflowState;

    @Column(name = "from_state", nullable = false, length = 100)
    private String fromState;

    @Column(name = "to_state", nullable = false, length = 100)
    private String toState;

    @Column(name = "transition_action", nullable = false, length = 100)
    private String transitionAction;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;

    @Column(name = "approval_chain_level", nullable = false)
    private Integer approvalChainLevel;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
