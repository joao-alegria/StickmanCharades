import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule, routingComponents } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';

import { CookieService } from 'ngx-cookie-service';

import { AppComponent } from './app.component';
import { FooterComponent } from './elements/footer/footer.component';
import { PreloaderComponent } from './elements/preloader/preloader.component';
import { BannerComponent } from './elements/banner/banner.component';
import { TeamComponent } from './elements/team/team.component';
import { FeaturesComponent } from './elements/features/features.component';
import { HeaderComponent } from './elements/header/header.component';
import { DefaultNavbarComponent } from './elements/header/default-navbar/default-navbar.component';
import { UserNavbarComponent } from './elements/header/user-navbar/user-navbar.component';
import { AdminNavbarComponent } from './elements/header/admin-navbar/admin-navbar.component';
import { ContactFormComponent } from './elements/forms/contact-form/contact-form.component';
import { ScrollupComponent } from './elements/scrollup/scrollup.component';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    PreloaderComponent,
    BannerComponent,
    TeamComponent,
    FeaturesComponent,
    HeaderComponent, DefaultNavbarComponent, UserNavbarComponent,
    routingComponents,
    AdminNavbarComponent,
    ContactFormComponent,
    ScrollupComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
