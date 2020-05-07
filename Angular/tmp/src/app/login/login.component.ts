import { Component, OnInit } from '@angular/core';
import { VsService } from "../vs.service";
import { Router } from '@angular/router';
import * as $ from 'jquery';
import { MessageService } from 'primeng/api';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [MessageService]
})
export class LoginComponent implements OnInit {

  constructor(private vs: VsService, private router: Router, private messageService: MessageService) { };

  ngOnInit() { }

  async login() {
    await this.vs.login($("#inputUsername").val().trim(), $("#inputPassword").val().trim()).catch(data => {
      if (data.status == 401) {
        this.messageService.add({ severity: 'warn', summary: 'Username/Password may be incorrect.' });
      }
      else if (data.status == 0) {
        this.messageService.add({ severity: 'warn', summary: 'Try later. Something went wrong.' });
      }
    })
    this.vs.whoami().then(data => {
      localStorage.setItem("username", data["username"])
      localStorage.setItem("role", data["role"])
      localStorage.setItem("isLogged", "true")
      if (localStorage.getItem("role") == "ADMIN") {
        $("#adminNav").show()
        $("#tenantNav").hide()
      } else {
        $("#adminNav").hide()
        $("#tenantNav").show()
      }
      $("#myNav").show()
      this.router.navigate(["/home"])
    }).catch(data => {
      localStorage.setItem("isLogged", "false")
    }).catch(error => {
      console.log(error)
    })
  }


}
