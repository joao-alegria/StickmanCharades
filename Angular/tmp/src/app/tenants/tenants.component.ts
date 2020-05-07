import { Component, OnInit } from '@angular/core';
import { VsService } from "../vs.service";
import { ModalserviceService } from '../modalservice.service';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

declare var $: any;

@Component({
  selector: 'app-tenants',
  templateUrl: './tenants.component.html',
  styleUrls: ['./tenants.component.css']
})
export class TenantsComponent implements OnInit {

  objectKeys = Object.keys;
  listData;
  selectedTenant
  selectedGroup

  constructor(private vs: VsService, private modal: ModalserviceService) {
    vs.checkLoggedIn()
  }

  ngOnInit() {
    this.listData = [];
    this.update()
  }

  newGroupBtnPressed(e) {
    e.stopPropagation()

    if ($("#groupNamePopup").css("display") == "block") {
      $("#groupNamePopup").css("display", "none")
    } else {
      $("#groupNamePopup").css("display", "block")

    }
  }

  async newGroup() {

    await this.vs.createNewGroup($("#groupName").val())

    $("#groupNamePopup").css("display", "none")
    $("#groupName").val("")

    this.update()
  }

  update() {
    this.vs.getGroups().then(data => { this.listData = data })
  }

  clickEvent(e, tenant, group) {
    e.stopPropagation()

    if ($("#tooltip-span").css("display") == "block") {
      $("#tooltip-span").css("display", "none")
      return
    }
    this.selectedTenant = tenant;
    this.selectedGroup = group;
    let tooltip = $("#tooltip-span");
    tooltip.css("display", "block")
    tooltip.css("position", "fixed")
    tooltip.css("overflow", "hidden")
    let x = e.clientX, y = e.clientY;
    tooltip.css("top", y - 10 + 'px');
    tooltip.css("left", x - 10 + 'px');
  }

  mouseLeftTooltip() {
    $("#tooltip-span").css("display", "none")
  }

  containerClick() {
    $("#tooltip-span").css("display", "none")
  }

  addTenant(event, group) {
    event.stopPropagation()
    this.modal.openNewTenantModal(this, group)
  }

  async deleteGroup(event, group) {
    event.stopPropagation()
    await this.vs.deleteGroup(group)
    this.update()
  }

  viewMoreInfo() {
    this.modal.openModal(this, this.selectedTenant, this.selectedGroup)
  }

  async deleteTenant() {
    await this.vs.deleteGroupTenant(this.selectedGroup, this.selectedTenant.username)
    this.mouseLeftTooltip()
    this.update()
  }

  viewSLA() {
    this.modal.openViewSlaModal(this, this.selectedGroup, this.selectedTenant)
    this.mouseLeftTooltip()
  }
}
