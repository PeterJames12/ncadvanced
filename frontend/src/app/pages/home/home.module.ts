import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {ReactiveFormsModule, FormsModule} from "@angular/forms";
import {AdminComponent} from "./admin/admin.component";
import {ManagerComponent} from "./manager/manager.component";
import {Ng2SmartTableModule} from "ng2-smart-table";
import {homeRoutes} from "./home.routes";
import {AssignRequestComponent} from "../../pages/home/manager/request-assign/assign-request.component"
import {JoinRequestComponent} from "../../pages/home/manager/request-join/join-request.component"
import {Ng2Bs3ModalModule} from "ng2-bs3-modal/ng2-bs3-modal";
import {DateParseModule} from "../../util/date-parser/date-parse.module";
import {Ng2GoogleChartsModule} from "ng2-google-charts";
import {FormTemplateModule} from "../../shared/form-template/form-template.module";
import {RequestModule} from "../../shared/request/request.module";
import {RequestFormComponent} from "./request-form/request-form.component";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(homeRoutes),
    ReactiveFormsModule,
    Ng2Bs3ModalModule,
    DateParseModule,
    Ng2SmartTableModule,
    Ng2GoogleChartsModule,
    FormTemplateModule,
    FormsModule,
    RequestModule
  ],
  declarations: [
    AssignRequestComponent,
    JoinRequestComponent,
    AdminComponent,
    ManagerComponent,
    RequestFormComponent,
  ],
  exports: [
    AssignRequestComponent,
    JoinRequestComponent,
    AdminComponent,
    ManagerComponent,
    RequestFormComponent,
  ]
})
export class HomeModule {
}
