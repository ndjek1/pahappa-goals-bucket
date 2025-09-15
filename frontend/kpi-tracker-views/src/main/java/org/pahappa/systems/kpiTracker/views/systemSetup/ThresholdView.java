package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ThresholdService;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.Threshold;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "thresholdsView")
@Getter
@Setter
@SessionScoped
public class ThresholdView extends PaginatedTableView<Threshold,ThresholdView,ThresholdView> {

    private ThresholdService thresholdService;
    private Search search;
    private double totalWeight;
    private boolean saved;
    private boolean updated;


    @PostConstruct
    public void init(){
        thresholdService= ApplicationContextProvider.getBean(ThresholdService.class);
        reloadFilterReset();

    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(thresholdService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE),i,i1));
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
        super.setTotalRecords(thresholdService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void showSuccessMessage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (this.saved) {
            // Message for creating a new department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Threshold  created successfully."));
            this.saved = false; // Reset the flag
        }

        if (this.updated) {
            // Message for updating an existing department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Threshold updated successfully."));
            this.updated = false; // Reset the flag
        }
    }

    public void deleteClient(Threshold threshold) {
        try {
            thresholdService.deleteInstance(threshold);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Threshold deleted successfully."));
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

}
