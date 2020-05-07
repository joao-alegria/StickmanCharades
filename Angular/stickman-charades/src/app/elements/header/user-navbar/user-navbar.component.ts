import { Component, OnInit } from '@angular/core';
import { VsService } from "../../../vs.service";
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-navbar',
  templateUrl: './user-navbar.component.html',
  styleUrls: ['./user-navbar.component.css']
})
export class UserNavbarComponent implements OnInit {

  constructor(private vs: VsService, private router: Router) { }

  ngOnInit(): void {
    if (this.router.url == "/download") {
      document.getElementById("download-tab").className = "active";
    } else if (this.router.url == "/contact") {
      document.getElementById("contact-tab").className = "active";
    }
  }

  async logout() {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("role");
    this.router.navigate(["/"]);
  }
}
