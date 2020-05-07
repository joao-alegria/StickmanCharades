import { Component, OnInit, Inject } from '@angular/core';
import { VsService } from '../vs.service';
import { MessageService } from 'primeng/api';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

declare var $: any;


@Component({
  selector: 'app-new-tenant-modal',
  templateUrl: './new-tenant-modal.component.html',
  styleUrls: ['./new-tenant-modal.component.css'],
  providers: [MessageService]
})
export class NewTenantModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<NewTenantModalComponent>,
    @Inject(MAT_DIALOG_DATA) private modalData: any,
    private vs: VsService, private messageService: MessageService
  ) {
    vs.checkLoggedIn()
  }

  ngOnInit() {
  }


  closeModal() {
    this.dialogRef.close();
  }

  passwordConfirmCheck(event) {
    if (event.key == "Enter") {
      this.submitTenant()
    } else {
      if ($("#inputPassword").val() != $("#inputPasswordConfirm").val() + event.key) {
        $("#inputPasswordConfirm").css("color", "red")
      } else {
        $("#inputPasswordConfirm").css("color", "green")
      }
    }
  }

  async submitTenant() {
    let username = $("#inputUsername").val()
    let password = $("#inputPassword").val()
    if (password != $("#inputPasswordConfirm").val()) {
      this.messageService.add({ severity: 'warn', summary: 'Password confirmation must match with Password.' });
    } else {
      await this.vs.createNewGroupTenant(this.modalData.group, { username: username, password: password })
      this.closeModal()
    }
  }
}
