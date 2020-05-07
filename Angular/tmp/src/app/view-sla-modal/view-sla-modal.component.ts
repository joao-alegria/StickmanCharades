import { Component, OnInit, Inject } from '@angular/core';
import { VsService } from '../vs.service';
import { MessageService } from 'primeng/api';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ModalserviceService } from '../modalservice.service';

declare var $: any;


@Component({
  selector: 'app-view-sla-modal',
  templateUrl: './view-sla-modal.component.html',
  styleUrls: ['./view-sla-modal.component.css'],
  providers: [MessageService]
})
export class ViewSlaModalComponent implements OnInit {

  validSLA;
  selectedSLA = []


  constructor(
    public dialogRef: MatDialogRef<ViewSlaModalComponent>,
    @Inject(MAT_DIALOG_DATA) private modalData: any,
    private vs: VsService, private messageService: MessageService, private modal: ModalserviceService
  ) {
    vs.checkLoggedIn()
  }

  ngOnInit() {
    this.validSLA = [];
    this.update()
  }

  closeModal() {
    this.dialogRef.close();
  }

  update() {
    this.vs.getSLA(this.modalData.group, this.modalData.tenant.username).then((data) => {
      this.validSLA = data
    })
  }

  registerSla(id) {
    let checkbox = $("#delete_" + id)
    if (checkbox.is(":checked") == true) {
      this.selectedSLA.push(id)
    } else {
      this.selectedSLA.splice(this.selectedSLA.indexOf(id), 1)
    }
  }


  async deleteSLA() {
    for (let slaId of this.selectedSLA) {
      await this.vs.deleteSLA(this.modalData.group, this.modalData.tenant.username, slaId)
    }
    this.selectedSLA = []
    this.update()
  }

}
