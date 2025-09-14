package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "reviewCycleView")
@Setter
@Getter
@SessionScoped
public class ReviewCycleView extends PaginatedTableView<ReviewCycle,ReviewCycleService,ReviewCycleService> {

    private ReviewCycleService reviewCycleService;
    private Search search;
    private String searchTerm;
    private ReviewCycleStatus selectedStatus;
    List<ReviewCycleStatus> reviewCycleStatusList;


    @PostConstruct
    public void init(){
        reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.reviewCycleStatusList = Arrays.asList(ReviewCycleStatus.values());
        reloadFilterReset();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(reviewCycleService.getInstances(this.search,i,i1));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public List load(int i, int i1, Map map, Map map1) {
        return null;
    }

    @Override
    public void reloadFilterReset(){
        this.search = new Search(ReviewCycle.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("title", "%" + searchTerm + "%");
        }
        if (selectedStatus != null) {
            search.addFilterEqual("status", selectedStatus);
        }

        super.setTotalRecords(reviewCycleService.countInstances(this.search));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }


    public void activateReviewCycle(ReviewCycle reviewCycle) {
        try {
            if (reviewCycle != null) {
                // Check if there’s already an ACTIVE cycle
                Search search = new Search(ReviewCycle.class);
                search.addFilterEqual("status", ReviewCycleStatus.ACTIVE);
                List<ReviewCycle> activeCycles = reviewCycleService.getInstances(search, 0, 0);

                if (!activeCycles.isEmpty()) {
                    ReviewCycle active = activeCycles.get(0); // assuming only one active at a time

                    Date today = new Date();
                    if (active.getEndDate() != null && active.getEndDate().after(today)) {
                        // Active cycle still running → cancel activation
                        FacesContext.getCurrentInstance().addMessage(
                                null,
                                new FacesMessage(FacesMessage.SEVERITY_WARN,"Activation Blocked",
                                "The current review cycle '" + active.getTitle() +
                                        "' is still active until " + active.getEndDate() + ". You cannot activate a new one yet."));
                        return;
                    } else {
                        // End the previous cycle
                        active.setStatus(ReviewCycleStatus.ENDED); // or CLOSED if you have that status
                        reviewCycleService.saveInstance(active);
                    }
                }

                // Activate the new one
                reviewCycle.setStatus(ReviewCycleStatus.ACTIVE);
                reviewCycleService.saveInstance(reviewCycle);

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,"Success", "Review cycle '" + reviewCycle.getTitle() + "' is now ACTIVE."));
                reloadFilterReset();
            }
        } catch (Exception e) {
            UiUtils.ComposeFailure("Activation Failed", e.getLocalizedMessage());
        }
    }


    public void deleteClient(ReviewCycle reviewCycle) {
        try {
            if(reviewCycle.getStatus() != ReviewCycleStatus.ACTIVE){
                reviewCycleService.deleteInstance(reviewCycle);
                reloadFilterReset();
            }else{
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Action Failed",
                                "Cannot delete an active review cycle.")
                );
            }

        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
