import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VsService } from "../vs.service";
import { ModalserviceService } from '../modalservice.service';


declare var $: any;

@Component({
  selector: 'app-blueprints',
  templateUrl: './blueprints.component.html',
  styleUrls: ['./blueprints.component.css']
})
export class BlueprintsComponent implements OnInit {

  objectKeys = Object.keys;
  listData;
  selectedElement
  tenant = true

  constructor(private vs: VsService, private route: ActivatedRoute, private modal: ModalserviceService) {
    this.vs.checkLoggedIn()
    if (localStorage.getItem("role") == "ADMIN") {
      this.tenant = false
    }
  }

  ngOnInit() {
    this.listData = [];
    this.update()
  }

  update() {
    this.vs.getBlueprints().then(data => { this.listData = data })
  }


  onboardVSB() {
    this.modal.openOnboardVsbModal(this)
  }

  onEnter(element) {
    $("#" + element).css("box-shadow", "4px 4px 10px grey")
  }

  onLeave(element) {
    $("#" + element).css("box-shadow", "none")
  }

  clickEvent(e, element) {
    e.stopPropagation()

    if ($("#tooltip-span").css("display") == "block") {
      $("#tooltip-span").css("display", "none")
      return
    }
    this.selectedElement = element;
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

  viewMoreInfo() {
    $("#tooltip-span").css("display", "none")
    this.modal.openModal(this, this.selectedElement, null)
  }

  containerClick() {
    $("#tooltip-span").css("display", "none")
  }


  async deleteBlueprint() {
    await this.vs.deleteVSB(this.selectedElement.vsBlueprintId)
    this.mouseLeftTooltip()
    this.update()
  }
}
