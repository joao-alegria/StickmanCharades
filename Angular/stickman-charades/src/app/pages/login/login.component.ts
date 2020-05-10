import { Component, OnInit } from '@angular/core';
import { VsService } from "../../vs.service";
import { Router } from '@angular/router';
import * as $ from 'jquery';
import { MessageService } from 'primeng/api';
import { Local } from 'protractor/built/driverProviders';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private vs: VsService, private router: Router) { }

  ngOnInit(): void {
  }

  async login() {
    await this.vs.login($("#username").val().trim(), $("#userpassword").val().trim()).catch(data => {
      if (data.status == 401) {
        document.getElementById("error").innerHTML = "Incorrect username / password. Please try again.";
        //this.messageService.add({ severity: 'warn', summary: 'Username/Password may be incorrect.' });
      } else if (data.status == 0) {
        //$("#error").text = "a";
        document.getElementById("error").innerHTML = "Incorrect username / password. Please try again.";
        //this.messageService.add({ severity: 'warn', summary: 'Try later. Something went wrong.' });
      } else {
        localStorage.setItem("loggedIn", "true");
        localStorage.setItem("role", "USER"); // ADMIN
        if (localStorage.getItem("role") == "ADMIN") {

          this.router.navigate(["/dashboard"]);
        } else {
          this.router.navigate(["/download"]);
        }
      }
      console.log(data);
    })
    // localStorage.setItem("loggedIn", "true");
    // localStorage.setItem("role", "ADMIN"); // USER / ADMIN
    // if(localStorage.getItem("role") == "ADMIN") {

    //   this.router.navigate(["/dashboard"]);
    // } else {
    //   this.router.navigate(["/download"]);
    // }
  }
}
