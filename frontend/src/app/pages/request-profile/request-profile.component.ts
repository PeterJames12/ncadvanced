import {Component, OnInit, ViewChild} from "@angular/core";
import {RequestService} from "../../service/request.service";
import {Request} from "../../model/request.model";
import {ActivatedRoute} from "@angular/router";
import {ToastsManager} from "ng2-toastr";
import {AuthService} from "../../service/auth.service";
import {User} from "../../model/user.model";
import {HistoryService} from "../../service/history.service";
import {History} from "../../model/history.model";
import {DeleteSubRequestComponent} from "./sub-request-delete/delete-sub-request.component";
import {AddSubRequestComponent} from "./sub-request-add/add-sub-request.component";

@Component({
  selector: 'request-profile',
  templateUrl: 'request-profile.component.html',
  styleUrls: ['request-profile.component.css']
})
export class RequestProfileComponent implements OnInit {

  currentUser: User;
  request: Request;
  showDescription: boolean = true;
  showHistory: boolean = true;
  showSubRequests: boolean = true;
  showJoinedRequests: boolean = true;
  historyRecords: History[];
  subRequests: Request[];

  @ViewChild(DeleteSubRequestComponent)
  deleteSubRequestComponent: DeleteSubRequestComponent;

  @ViewChild(AddSubRequestComponent)
  addSubRequestComponent: AddSubRequestComponent;

  constructor(private requestService: RequestService,
              private route: ActivatedRoute,
              private toastr: ToastsManager,
              private authService: AuthService,
              private historyService: HistoryService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      let id = +params['id'];

      this.historyService.getHistory(id).subscribe((historyRecords: History[]) => {
        this.historyRecords = historyRecords;
        console.log(historyRecords);
      });

      this.requestService.get(id).subscribe((request: Request) => {
        this.request = request;
        console.log(request)
      });

      this.requestService.getSubRequests(id).subscribe((subRequests: Request[]) => {
        this.subRequests = subRequests;
        console.log(subRequests)
      });
    });
    this.authService.currentUser.subscribe((user: User) => {
      this.currentUser = user;
    });
  }

  openAddSubRequestModal(): void {
    this.addSubRequestComponent.modal.open();
  }

  openDeleteSubRequestModal(subRequest: Request): void {
    this.deleteSubRequestComponent.subRequest = subRequest;
    this.deleteSubRequestComponent.modal.open();
  }

  updateSubRequests(subRequests: Request[]) {
    this.subRequests = subRequests;
  }

  changeShowDescription() {
    this.showDescription = !this.showDescription;
  }

  changeShowHistory() {
    this.showHistory = !this.showHistory;
  }

  changeShowSubRequests() {
    this.showSubRequests = !this.showSubRequests;
  }

  changeShowJoinedRequests() {
    this.showJoinedRequests = !this.showJoinedRequests;
  }

  updateRequest() {
    this.request.parentId = null;
    if (this.request.assignee.id === 0){
      this.request.assignee = <User>{};
    }
    this.request.lastChanger = this.currentUser;
    this.requestService.update(this.request)
      .subscribe(() => {
        this.toastr.success("Request updated", "Success")
      });
  }
}
