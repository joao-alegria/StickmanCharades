import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { TenantsComponent } from './tenants/tenants.component';
import { BlueprintsComponent } from './blueprints/blueprints.component';



const routes: Routes = [
  { path: 'tenants', component: TenantsComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'blueprints', component: BlueprintsComponent },
  { path: '', redirectTo: "home", pathMatch: "full" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
