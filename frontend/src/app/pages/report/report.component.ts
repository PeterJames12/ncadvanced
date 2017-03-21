import {Component, OnInit, ViewChild} from "@angular/core";
import {ToastsManager} from "ng2-toastr";
import {FormBuilder, FormGroup} from "@angular/forms";
import {UserService} from "../../service/user.service";
import {AuthService} from "../../service/auth.service";
import {CustomValidators} from "ng2-validation";
import {BarChartComponent} from "../../shared/bar-chart/bar-chart.component";
import {LineChartComponent} from "../../shared/line-chart/line-chart.component";
import {ReportService} from "../../service/report.service";
import * as FileSaver from "file-saver";

@Component({
  selector: 'report',
  templateUrl: 'report.component.html',
  styleUrls: ['report.component.css']
})

export class ReportComponent implements OnInit {

  private reportForm: FormGroup;

  @ViewChild(BarChartComponent)
  public barChart: BarChartComponent;

  // @ViewChild(BarChartComponent)
  // public barChart2: BarChartComponent;

  @ViewChild(LineChartComponent)
  public lineChart: LineChartComponent;
  private startdate: any;
  private enddate: any;

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              private reportService: ReportService,
              private authService: AuthService,
              private toastr: ToastsManager) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  validateField(field: string): boolean {
    return this.reportForm.get(field).valid || !this.reportForm.get(field).dirty;
  }

  private initForm(): void {
    this.reportForm = this.formBuilder.group({
      dateOfStart: ['', CustomValidators.dateISO],
      dateOfEnd: ['', CustomValidators.dateISO],
    });
  }

  private saveDates(formData) {
    if (formData.dateOfEnd > formData.dateOfStart) {
      this.startdate = formData.dateOfStart;
      this.enddate = formData.dateOfEnd;
      this.toastr.success("Data START: ".concat(this.startdate.toString() + ", Data END:" + this.enddate.toString()), "DATA:");
    }
    else {
      this.toastr.error("Error. Uncorrect dates: End date must be bigger than the start date");
    }
  }

  private generateReport() {
    this.barChart.build();
    this.lineChart.build();
  }


  private generatePDF() {
    this.reportService.getPDFReport().subscribe(
      data => {
        console.log(data);
        let blob = new Blob([data], {type: 'application/pdf'});
        // console.log(blob);
        console.log("ddddddd");
        FileSaver.saveAs(blob, "report.pdf");
        console.log("ddddddd22222");
        this.toastr.success("Report was created successfully", "Success!");
      }, e => this.handleError(e));
  }

  private handleError(error) {
    switch (error.status) {
      case 500:
        this.toastr.error("Can't create report", 'Error');
    }
  }

}
