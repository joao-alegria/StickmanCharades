import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import { Router } from '@angular/router';

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

    var requestHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Access-Control-Allow-Origin': "*", //this.endpoints["vsEndpoint"],
      'Authorization': "Basic " + btoa(username + ":" + password)
    });
    await this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["login"],
      {
        headers: requestHeaders, //new HttpHeaders().append('Content-Type', 'application/json').append("Authorization", "Basic " + btoa(username + ":" + password)),
        observe: 'response',
        withCredentials: true
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
