package org.pahappa.systems.kpiTracker.views.dialogs;

import org.pahappa.systems.kpiTracker.core.services.MyDemoService;
import org.pahappa.systems.kpiTracker.models.demo.MyDemo;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "myDialogForm")
@SessionScoped
public class MyDialogForm extends DialogForm<MyDemo>{
    private MyDemoService myDemoService;

    public MyDialogForm() {
        super(HyperLinks.NAME_DIALOG, 700, 800);
    }

    @PostConstruct
    public void init(){
        this.myDemoService = ApplicationContextProvider.getBean(MyDemoService.class);
    }

    @Override
    public void persist() throws Exception {
        myDemoService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new MyDemo();
    }
}
