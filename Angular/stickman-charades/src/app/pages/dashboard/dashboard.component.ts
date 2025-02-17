import { Component, OnInit } from '@angular/core';
import * as SockJS from '../../../assets/js/sockjs.min.js';
import * as Stomp from '../../../assets/js/stomp.min.js';
import { ViewChild } from "@angular/core";
import { NotifierService } from "angular-notifier";
import { VsService } from 'src/app/vs.service.js';

declare var $: any;

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  objectKeys = Object.keys;
  @ViewChild("customNotification", { static: true }) customNotificationTmpl;
  sessions
  currentSession = -1
  client: any

  constructor(private notifierService: NotifierService, private vs: VsService) { }

  ngOnInit(): void {
    this.vs.getEndpoints().then(endpoints => {
      var wsocket = new SockJS(endpoints["websocket"] + '/game/skeletons', { headers: { 'Authorization': 'Basic ' + localStorage.getItem("TOKEN") } });
      this.client = Stomp.Stomp.over(wsocket);
      let _this = this
      _this.client.connect({}, function (frame) {
        _this.client.subscribe('/game/admin', message => {
          console.info(message)
          _this.notify(JSON.parse(message.body))
        });
      });
      this.sessions = [];
      this.update();
    })

    $("#myModal").on("hidden.bs.modal", function () {
      this.update()
    });
  }

  notify(message) {
    this.notifierService.show({
      type: "warning",
      message: message,
      template: this.customNotificationTmpl
    })
  }

  async update() {
    await this.vs.getSessions().then((data) => { this.sessions = data.body["sessions"] })
  }

  notificationClicked(data) {
    console.log(data)
    this.goToSession(data["session"].split("_")[1])
  }

  goToSession(session) {
    this.currentSession = session
    $('#modalTitle').text("Session: " + session)
    $('#myModal').modal('toggle')
  }

}
