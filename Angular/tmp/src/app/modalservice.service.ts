import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ModalComponent } from './modal/modal.component';
import { VsService } from './vs.service';
import { NewTenantModalComponent } from './new-tenant-modal/new-tenant-modal.component';
import { OnboardVsbModalComponent } from './onboard-vsb-modal/onboard-vsb-modal.component';
import { ViewSlaModalComponent } from './view-sla-modal/view-sla-modal.component';

declare var $: any;


@Injectable({
  providedIn: 'root'
})
export class ModalserviceService {


  constructor(private matDialog: MatDialog, private vs: VsService) { }

  /**
   * Used to identify the class that asked for the modal and add the corresponding buttons and show the modal
   * @param elementRef 
   * @param data 
   */
  openModal(elementRef, data, group) {
    let title = ""
    switch (elementRef.constructor.name) {
      case "TenantsComponent":
        title = data.username
        break
      case "BlueprintsComponent":
        title = data.name
        break

    }

    const dialogConfig = new MatDialogConfig();
    // The user can't close the dialog by clicking outside its body
    dialogConfig.id = "modal-component";
    dialogConfig.height = "80%";
    dialogConfig.width = "80%";
    dialogConfig.data = {
      title: title,
      data: data,
      group: group
    }
    // https://material.angular.io/components/dialog/overview
    const modalDialog = this.matDialog.open(ModalComponent, dialogConfig);
    modalDialog.afterClosed().toPromise().then(() => {
      elementRef.update()
    })
  }

  openNewTenantModal(elementRef, group) {

    const dialogConfig = new MatDialogConfig();
    // The user can't close the dialog by clicking outside its body
    dialogConfig.id = "new-tenant-modal-component";
    dialogConfig.height = "80%";
    dialogConfig.width = "80%";
    dialogConfig.data = {
      group: group,
    }
    // https://material.angular.io/components/dialog/overview
    const modalDialog = this.matDialog.open(NewTenantModalComponent, dialogConfig);
    modalDialog.afterClosed().toPromise().then(() => {
      elementRef.update()
    })

  }

  openOnboardVsbModal(elementRef) {

    const dialogConfig = new MatDialogConfig();
    // The user can't close the dialog by clicking outside its body
    dialogConfig.id = "onboard-vsb-modal-component";
    dialogConfig.height = "80%";
    dialogConfig.width = "80%";
    dialogConfig.data = {
    }
    // https://material.angular.io/components/dialog/overview
    const modalDialog = this.matDialog.open(OnboardVsbModalComponent, dialogConfig);
    modalDialog.afterClosed().toPromise().then(() => {
      elementRef.update()
    })
  }

  openViewSlaModal(elementRef, group, tenant) {

    const dialogConfig = new MatDialogConfig();
    // The user can't close the dialog by clicking outside its body
    dialogConfig.id = "view-descriptor-modal-component";
    dialogConfig.height = "80%";
    dialogConfig.width = "80%";
    dialogConfig.data = {
      group: group,
      tenant: tenant
    }
    // https://material.angular.io/components/dialog/overview
    const modalDialog = this.matDialog.open(ViewSlaModalComponent, dialogConfig);
    modalDialog.beforeClosed().toPromise().then(() => {
      // elementRef.closeModal()
      elementRef.update()

    })
  }

}
