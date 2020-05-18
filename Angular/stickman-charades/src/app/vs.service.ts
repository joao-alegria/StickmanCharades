import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import { Router } from '@angular/router';
import { toBase64String } from '@angular/compiler/src/output/source_map';

declare var $: any;

@Injectable({
  providedIn: 'root'
})
export class VsService {

  private endpoints: Object;

  constructor(private http: HttpClient, private router: Router, private ngZone: NgZone) { }

  private async loadEndpoints() {
    return this.http.get("assets/endpoints.json").toPromise()
  }

  async login(username: string, password: string) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }

    let headers = new HttpHeaders();
    headers = headers.append("Authorization", "Basic " + btoa(username + ":" + password));
    await this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["login"],
      {
        headers: headers,
        observe: 'response'
      }
    ).toPromise();
  }

  async whoami() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["whoami"], { withCredentials: true }).toPromise();
  }

  async checkLoggedIn() {
    await $(document).ready(() => this.ngZone.run(() => {
      this.whoami().then(data => {
        localStorage.setItem("role", data["role"])
        localStorage.setItem("username", data["username"])
        if (localStorage.getItem("role") == "ADMIN") {
          $("#adminNav").show()
          $("#tenantNav").hide()
        } else {
          $("#adminNav").hide()
          $("#tenantNav").show()
        }
        $("#myNav").show()
        return data["role"]
      }).catch(error => {
        $("#myNav").hide()
        this.router.navigate(["/", "login"])
      })
    }))
  }

  async register(firstname: string, lastname: string, email: string, username: string, password: string, accountType: string) {

  }

  async getBlueprints() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsblueprints"], { withCredentials: true }).toPromise();
  }
}
