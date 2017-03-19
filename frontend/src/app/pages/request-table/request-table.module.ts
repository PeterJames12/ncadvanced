import {CommonModule} from "@angular/common";
import {ReactiveFormsModule, FormsModule} from "@angular/forms";
import {NgModule} from "@angular/core/src/metadata/ng_module";
import {Routes, RouterModule} from "@angular/router";
import {RequestTableComponent} from "./request-table.component";
import {FormTemplateModule} from "../../shared/form-template/form-template.module";
import {RequestModule} from "../../shared/request/request.module";
import {DateParseModule} from "../../util/date-parser/date-parse.module";
import {DeleteRequestComponent} from "./request-delete/delete-request.component";
import {Ng2Bs3ModalModule} from "ng2-bs3-modal/ng2-bs3-modal";
import {RequestSortPipe} from "../../pipes/request-sort.pipe";
import {RequestFilterPipe} from "../../pipes/request-filter.pipe";

const routes: Routes = [
  {path: '', component: RequestTableComponent}
];

@NgModule({
  imports: [
    DateParseModule,
    CommonModule,
    RouterModule.forChild(routes),
    FormTemplateModule,
    RequestModule,
    ReactiveFormsModule,
    Ng2Bs3ModalModule,
    FormsModule
  ],
  declarations: [
    RequestTableComponent,
    DeleteRequestComponent,
    RequestFilterPipe,
    RequestSortPipe
  ],
  exports: [
    RequestTableComponent,
    DeleteRequestComponent
  ]
})
export class RequestTableModule {
}
