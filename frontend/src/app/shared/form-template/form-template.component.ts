import {Component} from "@angular/core";

@Component({
  selector: 'form-template',
  template:
    `<div class="panel panel-default">
      <div class="panel-heading">
        <strong>
          <ng-content select=".template-header"></ng-content>
        </strong>
      </div>
      <div class="panel-body">
        <ng-content select=".template-body"></ng-content>
      </div>
      <div class="panel-footer">
        <div class="login-helper">
          <ng-content select=".template-footer"></ng-content>
        </div>
      </div>
    </div>`
})
export class FormTemplateComponent {
}