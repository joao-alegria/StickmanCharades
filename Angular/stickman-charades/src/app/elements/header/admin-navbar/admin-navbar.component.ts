import { Component, OnInit } from '@angular/core';
import { VsService } from "../../../vs.service";
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-navbar',
  templateUrl: './admin-navbar.component.html',
  styleUrls: ['./admin-navbar.component.css']
})
export class AdminNavbarComponent implements OnInit {

  constructor(private vs: VsService, private router: Router) { }

  ngOnInit(): void {
    if (this.router.url == "/dashboard") {
      document.getElementById("dashboard-tab").className = "active";
    }
  }

  async logout() {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("role");
    this.router.navigate(["/"]);
  }

}
