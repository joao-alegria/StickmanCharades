import { Component, OnInit } from '@angular/core';
import * as $ from 'jquery';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    console.log("Logged In: " + localStorage.getItem("loggedIn"));
    if(localStorage.getItem("loggedIn")===null || localStorage.getItem("loggedIn")=="false") {
      $("#default-navbar").show();
      $("#user-navbar").hide();
      $("#admin-navbar").hide();
    } else {
      $("#default-navbar").hide();
      if(localStorage.getItem("role")=="ADMIN") {
        $("#user-navbar").hide();
        $("#admin-navbar").show();
      } else {
        $("#user-navbar").show();
        $("#admin-navbar").hide();
      }
    }
  }

}
