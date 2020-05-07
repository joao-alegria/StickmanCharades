import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { VsService } from '../vs.service';

declare var $: any;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  groups
  tenants
  blueprints
  descriptors
  netslices
  vsinstances

  isTenant = true
  userName
  constructor(private vs: VsService, private router: Router) {
    this.vs.checkLoggedIn()
    this.userName = localStorage.getItem("username")
    if (localStorage.getItem("role") == "ADMIN") {
      this.isTenant = false
    }
  }

  ngOnInit() {
    this.update()
  }

  update() {
    this.vs.getGroups().then(data => this.groups = JSON.parse(JSON.stringify(data)).length)
    this.vs.getAllTenants().then(data => this.tenants = JSON.parse(JSON.stringify(data)).length)
    this.vs.getBlueprints().then(data => this.blueprints = JSON.parse(JSON.stringify(data)).length)
    this.vs.getDescriptors().then(data => this.descriptors = JSON.parse(JSON.stringify(data)).length)
    this.vs.getNetSlices().then(data => this.netslices = JSON.parse(JSON.stringify(data)).length)
    this.vs.getVerticalSlices().then(data => this.vsinstances = JSON.parse(JSON.stringify(data)).length)
  }

  goToGroups() {
    this.router.navigate(["/tenants"])
  }

  goToBlueprints() {
    this.router.navigate(["/blueprints"])
  }

  goToDescriptors() {
    this.router.navigate(["/descriptors"])
  }

  goToInstances() {
    this.router.navigate(["/vsinstances"])
  }

  goToNet() {
    this.router.navigate(["/netslices"])
  }

  onEnter(element) {
    $("#" + element).css("box-shadow", "4px 4px 10px grey")
  }

  onLeave(element) {
    $("#" + element).css("box-shadow", "none")
  }

}
