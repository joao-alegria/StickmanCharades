import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-default-navbar',
  templateUrl: './default-navbar.component.html',
  styleUrls: ['./default-navbar.component.css']
})
export class DefaultNavbarComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
    if (this.router.url == "/") {
      document.getElementById("home-tab").className = "active";
    } else if (this.router.url == "/about") {
      document.getElementById("about-tab").className = "active";
    } else if (this.router.url == "/contact") {
      document.getElementById("contact-tab").className = "active";
    } else if (this.router.url.includes("/documentation")) {
      document.getElementById("documentation-tab").className = "active";
    }
    else if (this.router.url == "/demo") {
      document.getElementById("demo-tab").className = "active";
    }
  }

}
