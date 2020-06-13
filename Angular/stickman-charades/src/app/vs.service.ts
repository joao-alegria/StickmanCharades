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

  async getEndpoints() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }

    return this.endpoints
  }

  async login(username: string, password: string) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }

    let headers = new HttpHeaders();
    headers = headers.append("Authorization", "Basic " + btoa(username + ":" + password));
    return await this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["login"],
      {
        headers: headers,
        observe: 'response'
      }
    ).toPromise();
  }

  async getSessions() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }

    let headers = new HttpHeaders();
    headers = headers.append("Authorization", "Basic " + localStorage.getItem("TOKEN"));

    let params = new HttpParams();
    params = params.append("active", "True");

    return await this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["session"],
      {
        params: params,
        headers: headers,
        observe: 'response'
      }
    ).toPromise();
  }


  async register(firstname: string, lastname: string, email: string, username: string, password: string) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }

    let data = {}
    data["username"] = username
    data["password"] = password
    data["email"] = email

    return await this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["register"], data, { observe: 'response' }).toPromise();
  }

}
