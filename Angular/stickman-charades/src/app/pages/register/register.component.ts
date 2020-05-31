import { Component, OnInit } from '@angular/core';
import { VsService } from "../../vs.service";
import { Router } from '@angular/router';
import * as $ from 'jquery';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  constructor(private vs: VsService, private router: Router) { }

  ngOnInit(): void {
  }

  async register() {

    if ($("#userpassword").val().trim() != $("#passwordconfirmation").val().trim()) {
      document.getElementById("error").innerHTML = "Passwords don't match. Please try again.";
      return;
    }

    var accountType = "ADMIN";
    if ($("#regularRadio").checked == true) {
      accountType = "USER";
    }

    await this.vs.register($("#firstname").val().trim(), $("#lastname").val().trim(), $("#useremail").val().trim(), $("#username").val().trim(), $("#userpassword").val().trim(), accountType).catch(async () => {

      await this.vs.login($("#username").val().trim(), $("#userpassword").val().trim()).then(data => {
        localStorage.setItem("loggedIn", "true");
        if (data.body["admin"]) {
          localStorage.setItem("role", "ADMIN");
        } else {
          localStorage.setItem("role", "USER");
        }
        if (localStorage.getItem("role") == "ADMIN") {
          this.router.navigate(["/dashboard"]);
        } else {
          this.router.navigate(["/download"]);
        }
      }).catch(data => {
        if (data.status == 401) {
          document.getElementById("error").innerHTML = "Incorrect username / password. Please try again.";
          //this.messageService.add({ severity: 'warn', summary: 'Username/Password may be incorrect.' });
        } else if (data.status == 0) {
          document.getElementById("error").innerHTML = "Something went wrong. Please try again later.";
        }
      })
    })

  }

}
