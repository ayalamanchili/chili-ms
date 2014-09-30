/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.employee.prefeval;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.JSONUtils;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;

import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.employee.AbstractWizard;
import info.yalamanchili.office.client.employee.AbstractWizard.AbstractStep;
import info.yalamanchili.office.client.employee.prefeval.CreatePerformanceEvaluationPanel.CreatePerformanceEvaluationPanelType;
import info.yalamanchili.office.client.ext.question.QuestionCategory;
import info.yalamanchili.office.client.ext.question.QuestionContext;
import java.util.logging.Logger;

/**
 *
 * @author ayalamanchili
 */
public class PerformanceEvaluationWizard extends AbstractWizard {

    private static Logger logger = Logger.getLogger(PerformanceEvaluationWizard.class.getName());

    public enum PerformanceEvaluationWizardType {

        SELF_MANAGER, MANAGER;
    }

    protected PerformanceEvaluationWizardType type;
    protected String employeeId;
    protected String year;
    protected CreatePerformanceEvaluationStep perfEvalStartStep;
    protected CreatePerformanceEvaluationStep perfEvalEndStep;
    CreateQuestionCommentsWidgetStep selfReviewQuestionsStep;
    CreateQuestionCommentsWidgetStep skillQuestionsStep;
    CreateQuestionCommentsWidgetStep attitudeQuestionsStep;
    CreateQuestionCommentsWidgetStep managementQuestionsStep;

    private static PerformanceEvaluationWizard instance;

    public static PerformanceEvaluationWizard instance() {
        return instance;
    }

    public PerformanceEvaluationWizard(PerformanceEvaluationWizardType type) {
        instance = this;
        this.type = type;
        initWizard();
    }

    public PerformanceEvaluationWizard(PerformanceEvaluationWizardType type, String employeeId) {
        instance = this;
        this.employeeId = employeeId;
        this.type = type;
        initWizard();
    }

    public PerformanceEvaluationWizard(String employeeId, String year) {
        instance = this;
        this.employeeId = employeeId;
        this.year = year;
        initWizard();
    }

    @Override
    protected void initSteps() {
        perfEvalStartStep = new CreatePerformanceEvaluationStep(CreatePerformanceEvaluationPanelType.Start.name(), CreatePerformanceEvaluationPanelType.Start.name());
        if (PerformanceEvaluationWizardType.SELF_MANAGER.equals(type)) {
            selfReviewQuestionsStep = new CreateQuestionCommentsWidgetStep(QuestionCategory.SELF_EVALUATION.name(), QuestionCategory.SELF_EVALUATION.name());
        }
        skillQuestionsStep = new CreateQuestionCommentsWidgetStep(QuestionCategory.SKILL_AND_APTITUDE.name(), QuestionCategory.SKILL_AND_APTITUDE.name());
        attitudeQuestionsStep = new CreateQuestionCommentsWidgetStep(QuestionCategory.ATTITUDE.name(), QuestionCategory.ATTITUDE.name());
        managementQuestionsStep = new CreateQuestionCommentsWidgetStep(QuestionCategory.MANAGEMENT.name(), QuestionCategory.MANAGEMENT.name());
        perfEvalEndStep = new CreatePerformanceEvaluationStep(CreatePerformanceEvaluationPanelType.End.name(), CreatePerformanceEvaluationPanelType.End.name());

        steps.add(perfEvalStartStep);
        if (PerformanceEvaluationWizardType.SELF_MANAGER.equals(type)) {
            steps.add(selfReviewQuestionsStep);
        }
        steps.add(skillQuestionsStep);
        steps.add(attitudeQuestionsStep);
        steps.add(managementQuestionsStep);
        steps.add(perfEvalEndStep);
    }

    public class CreatePerformanceEvaluationStep extends AbstractStep<CreatePerformanceEvaluationPanel> {

        public CreatePerformanceEvaluationStep(String stepId, String stepName) {
            super(stepId, stepName);
        }

        @Override
        public CreatePerformanceEvaluationPanel getWidget() {
            if (widget == null) {
                if (stepId.equals(CreatePerformanceEvaluationPanelType.Start.name())) {
                    widget = new CreatePerformanceEvaluationPanel(CreatePerformanceEvaluationPanelType.Start);
                }
                if (stepId.equals(CreatePerformanceEvaluationPanelType.End.name())) {
                    widget = new CreatePerformanceEvaluationPanel(CreatePerformanceEvaluationPanelType.End);
                }
            }
            return widget;
        }

        @Override
        protected boolean isLastStep() {
            if (stepId.equals(CreatePerformanceEvaluationPanelType.End.name())) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public JSONObject getValue() {
            return widget.populateEntityFromFields();
        }

        protected void complete() {
            HttpService.HttpServiceAsync.instance().doPut(getCompleteUrl(), populateEntity().toString(), OfficeWelcome.instance().getHeaders(), true,
                    new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable res) {
                            getWidget().handleErrorResponse(res);
                        }

                        @Override
                        public void onSuccess(String res) {
                            new ResponseStatusWidget().show("Successfully Created Performance Evaluation");
                            TabPanel.instance().myOfficePanel.entityPanel.clear();
                            TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllPerformanceEvaluationPanel(employeeId));
                        }
                    });
        }

        protected String getCompleteUrl() {
            if (PerformanceEvaluationWizardType.SELF_MANAGER.equals(type)) {
                return OfficeWelcome.constants.root_url() + "performance-evaluation/associate/save-review?submitForApproval=" + perfEvalEndStep.getWidget().getSubmitForApproval();
            } else {
                return OfficeWelcome.constants.root_url() + "performance-evaluation/create";
            }
        }

        protected JSONObject populateEntity() {
            JSONObject entity = new JSONObject();
            JSONObject perfEvalStartObj = perfEvalStartStep.getWidget().populateEntityFromFields();
            JSONObject perfEvalEndObj = perfEvalEndStep.getWidget().populateEntityFromFields();
            entity.put("performanceEvaluation", JSONUtils.merge(perfEvalStartObj, perfEvalEndObj));
            JSONArray selfReviewQuestions = selfReviewQuestionsStep.getWidget().getValues();
            JSONArray skillQuestions = skillQuestionsStep.getWidget().getValues();
            JSONArray attitudeQuestions = attitudeQuestionsStep.getWidget().getValues();
            JSONArray managementQuestions = managementQuestionsStep.getWidget().getValues();
            JSONArray questionComments = new JSONArray();
            int x = 0;
            for (int i = 0; i < selfReviewQuestions.size(); i++) {
                questionComments.set(x, selfReviewQuestions.get(i));
                x++;
            }
            for (int i = 0; i < attitudeQuestions.size(); i++) {
                questionComments.set(x, attitudeQuestions.get(i));
                x++;
            }
            for (int i = 0; i < skillQuestions.size(); i++) {
                questionComments.set(x, skillQuestions.get(i));
                x++;
            }
            for (int i = 0; i < managementQuestions.size(); i++) {
                questionComments.set(x, managementQuestions.get(i));
                x++;
            }
            entity.put("comments", questionComments);
            logger.info(entity.toString());
            return entity;
        }

        @Override
        protected String nextButtonText() {
            if (stepId.equals(CreatePerformanceEvaluationPanelType.End.name())) {
                return "Complete";
            }
            return "Next";
        }

        @Override
        protected void onLoad() {
            widget.loadData();
        }

        @Override
        protected void validate() {
            getWidget().clearMessages();
            if (stepId.equals(CreatePerformanceEvaluationPanelType.Start.name()) && perfEvalStartStep.getWidget().getYearField().getValue() == null) {
                perfEvalStartStep.getWidget().getYearField().setMessage("Select a Year");
            }
            nextClicked();
        }

        protected String getValidateUrl() {
            return OfficeWelcome.constants.root_url() + "performance-evaluation/validate";
        }
    }

    public class CreateQuestionCommentsWidgetStep extends AbstractStep<CreateQuestionCommentsWidget> {

        public CreateQuestionCommentsWidgetStep(String stepId, String stepName) {
            super(stepId, stepName);
        }

        @Override
        public CreateQuestionCommentsWidget getWidget() {
            if (widget == null) {
                if (stepId.equals(QuestionCategory.SELF_EVALUATION.name())) {
                    widget = new CreateQuestionCommentsWidget(QuestionCategory.SELF_EVALUATION, QuestionContext.PERFORMANCE_EVALUATION_SELF, false, true);
                }
                if (stepId.equals(QuestionCategory.SKILL_AND_APTITUDE.name())) {
                    widget = new CreateQuestionCommentsWidget(QuestionCategory.SKILL_AND_APTITUDE, QuestionContext.PERFORMANCE_EVALUATION_MANGER);
                }
                if (stepId.equals(QuestionCategory.ATTITUDE.name())) {
                    widget = new CreateQuestionCommentsWidget(QuestionCategory.ATTITUDE, QuestionContext.PERFORMANCE_EVALUATION_MANGER);
                }
                if (stepId.equals(QuestionCategory.MANAGEMENT.name())) {
                    widget = new CreateQuestionCommentsWidget(QuestionCategory.MANAGEMENT, QuestionContext.PERFORMANCE_EVALUATION_MANGER);
                }
            }
            return widget;
        }

        @Override
        public JSONValue getValue() {
            return widget.getValues();
        }

        @Override
        protected void onLoad() {
            widget.loadQuestions();
        }

        @Override
        protected void validate() {
            if (getWidget().validate()) {
                getWidget().clearMessages();
                nextClicked();
            }

        }
    }

}
