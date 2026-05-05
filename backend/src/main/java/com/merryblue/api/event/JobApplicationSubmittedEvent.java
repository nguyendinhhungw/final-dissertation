package com.merryblue.api.event;

import com.merryblue.api.model.JobApplication;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JobApplicationSubmittedEvent extends ApplicationEvent {
    private final JobApplication application;

    public JobApplicationSubmittedEvent(Object source, JobApplication application) {
        super(source);
        this.application = application;
    }
}
