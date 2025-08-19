package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "kpiView")
@Getter
@Setter
@SessionScoped
public class KPIView implements Serializable {

    private static final long serialVersionUID = 1L;

    private KpisService kpisService;
    private List<KPI> dataModels;
    private String searchTerm;
    private Date createdFrom, createdTo;
    private String dataEmptyMessage = "No KPIs found.";

    @PostConstruct
    public void init() {
        kpisService = ApplicationContextProvider.getBean(KpisService.class);
        reloadFilterReset();
    }

    public void reloadFilterReset() {
        Search kpiSearch = new Search();
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            kpiSearch.addFilterILike("name", "%" + searchTerm + "%");
        }
        
        // Filter and load dataModels based on search/filter criteria
        this.dataModels = kpisService.getInstances(kpiSearch, 0, 1000);
    }

    public void reloadFromDB(int i, int i1, java.util.Map<String, Object> map) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("name", "%" + searchTerm + "%");
        }
        
        this.dataModels = kpisService.getInstances(search, i, i1);
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public List load(int i, int i1, java.util.Map map, java.util.Map map1) {
        return null;
    }

    public void deleteKPI(KPI kpi) {
        try {
            kpisService.deleteInstance(kpi);
            reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
