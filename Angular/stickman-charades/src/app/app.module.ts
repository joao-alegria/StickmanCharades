import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
import { FooterComponent } from './elements/footer/footer.component';
import { PreloaderComponent } from './elements/preloader/preloader.component';
import { HeaderComponent } from './elements/header/header.component';
import { BannerComponent } from './elements/banner/banner.component';
import { TeamComponent } from './elements/team/team.component';
import { FeaturesComponent } from './elements/features/features.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FooterComponent,
    PreloaderComponent,
    HeaderComponent,
    BannerComponent,
    TeamComponent,
    FeaturesComponent,
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
