package org.pahappa.systems.kpiTracker.views.systemSetup;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;

import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "globalWeightView")
@Setter
@Getter
@SessionScoped
public class GlobalWeightView extends PaginatedTableView<GlobalWeight, GlobalWeightService, GlobalWeightService> {
    private GlobalWeightService globalWeightService;
    private Search search;
    private boolean createWeight;

    @PostConstruct
    public void init(){
        globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
        canCreateNewWeights();
        reloadFilterReset();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(globalWeightService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE),i,i1));
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
        super.setTotalRecords(globalWeightService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }

    public void deleteClient(GlobalWeight globalWeight) {
        try {
            globalWeightService.deleteInstance(globalWeight);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

    public  void canCreateNewWeights(){
        Search search = new Search(GlobalWeight.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("reviewCycle.status", ReviewCycleStatus.ACTIVE)
        );
        List<GlobalWeight> weights = globalWeightService.getInstances(search,0,0);
        this.createWeight = weights.isEmpty();
    }
}
