import { BrowserModule } from '@angular/platform-browser';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
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
import { SkeletonComponent } from './elements/skeleton/skeleton.component';

import { NotifierModule } from "angular-notifier";
import { DocumentationComponent } from './pages/documentation/documentation.component';

@NgModule({
  declarations: [
    AppComponent,
    PreloaderComponent, ScrollupComponent,
    HeaderComponent, DefaultNavbarComponent, UserNavbarComponent, AdminNavbarComponent,
    FooterComponent,
    BannerComponent, FeaturesComponent, TeamComponent,
    ContactFormComponent,
    routingComponents,
    SkeletonComponent,
    DocumentationComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotifierModule.withConfig({
      position: {
        horizontal: {
          position: "right",
          distance: 12
        },
        vertical: {
          position: "top",
          distance: 12,
          gap: 10
        }
      },
      theme: "material",
      behaviour: {
        autoHide: 3000,
        onClick: false,
        onMouseover: "pauseAutoHide",
        showDismissButton: true,
        stacking: 4
      },
      animations: {
        enabled: true,
        show: {
          preset: "slide",
          speed: 300,
          easing: "ease"
        },
        hide: {
          preset: "fade",
          speed: 300,
          easing: "ease",
          offset: 50
        },
        shift: {
          speed: 300,
          easing: "ease"
        },
        overlap: 150
      }
    }),
  ],
  providers: [CookieService],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }
