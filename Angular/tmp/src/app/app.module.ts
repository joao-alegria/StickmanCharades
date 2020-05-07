import { BrowserModule } from '@angular/platform-browser';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

import { ToastModule } from 'primeng/toast';
import { ChipsModule } from 'primeng/chips';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { TenantsComponent } from './tenants/tenants.component';
import { BlueprintsComponent } from './blueprints/blueprints.component';
import { ModalComponent } from './modal/modal.component';
import { ModalserviceService } from './modalservice.service';

import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { NgxFileDropModule } from 'ngx-file-drop';
import { NewTenantModalComponent } from './new-tenant-modal/new-tenant-modal.component';
import { OnboardVsbModalComponent } from './onboard-vsb-modal/onboard-vsb-modal.component';
import { ViewSlaModalComponent } from './view-sla-modal/view-sla-modal.component';



@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    TenantsComponent,
    BlueprintsComponent,
    ModalComponent,
    NewTenantModalComponent,
    OnboardVsbModalComponent,
    ViewSlaModalComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ToastModule,
    MatDialogModule,
    NgxFileDropModule,
    ChipsModule
  ],
  providers: [CookieService, ModalserviceService],
  bootstrap: [AppComponent],
  entryComponents: [
    ModalComponent, NewTenantModalComponent, OnboardVsbModalComponent, ViewSlaModalComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AppModule { }
