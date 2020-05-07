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

    const body = new HttpParams()
      .set('username', username)
      .set('password', password);
    await this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["login"],
      body.toString(),
      {
        headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
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

  async getDescriptors() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsdescriptors"], { withCredentials: true }).toPromise();
  }

  async getBlueprints() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsblueprints"], { withCredentials: true }).toPromise();
  }

  async getGroups() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsgroups"], { withCredentials: true }).toPromise();
  }

  async getAllTenants() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vstenants"], { withCredentials: true }).toPromise();
  }

  async getNSTemplates() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsnstemplates"], { withCredentials: true }).toPromise();
  }

  async getNetSlices() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsnetworkslices"], { withCredentials: true }).toPromise();
  }

  async getVerticalSlices() {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    let allVerticalSlices = []
    await this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslices"], { withCredentials: true }).toPromise().then(async (data) => {
      for (let element in data) {
        await this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslicesID"] + "/" + data[element], { withCredentials: true }).toPromise().then((verticalSlice) => {
          allVerticalSlices.push(verticalSlice)
        })
      }
    });
    return allVerticalSlices
  }

  async createNewGroup(name) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsnewgroup"] + name, "", { withCredentials: true }).toPromise();
  }

  async deleteGroup(group) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsnewgroup"] + group, { withCredentials: true }).toPromise();
  }

  async createNewGroupTenant(group, data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsnewgroup"] + group + "/tenant", data, { withCredentials: true }).toPromise();
  }

  async deleteGroupTenant(group, tenant) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsnewgroup"] + group + "/tenant/" + tenant, { withCredentials: true }).toPromise();
  }

  async onboardVSB(data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsblueprints"], data, { withCredentials: true }).toPromise();
  }

  async onboardNST(data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsnst"], data, { withCredentials: true }).toPromise();
  }

  async deleteNST(nstId) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsnst"] + "/" + nstId, { withCredentials: true }).toPromise();
  }

  async instantiateNewNS(data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsnetworkslices"], data, { withCredentials: true }).toPromise();
  }

  async deleteVSB(blueprintID) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsblueprints"] + "/" + blueprintID, { withCredentials: true }).toPromise();
  }

  async createNewDescriptor(data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsdescriptors"], data, { withCredentials: true }).toPromise();
  }

  async deleteDescriptor(descriptorId) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsdescriptors"] + "/" + descriptorId, { withCredentials: true }).toPromise();
  }

  async instantiateNewVS(data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslicesID"], data, { withCredentials: true }).toPromise();
  }

  async terminateVSI(vsiId) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslicesID"] + "/" + vsiId + "/terminate", "", { withCredentials: true }).toPromise();
  }

  async deleteVSI(vsiId) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslicesID"] + "/" + vsiId, { withCredentials: true }).toPromise();
  }

  async scaleVSI(vsiId, data) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.put(this.endpoints["vsEndpoint"] + this.endpoints["vsverticalslicesID"] + "/" + vsiId, data, { withCredentials: true }).toPromise();
  }

  async createSLA(group, tenant, sla) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.post(this.endpoints["vsEndpoint"] + this.endpoints["vsgroups"] + "/" + group + "/tenant/" + tenant + "/sla", sla, { withCredentials: true }).toPromise();
  }

  async getSLA(group, tenant) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.get(this.endpoints["vsEndpoint"] + this.endpoints["vsgroups"] + "/" + group + "/tenant/" + tenant + "/sla", { withCredentials: true }).toPromise();
  }

  async deleteSLA(group, tenant, id) {
    if (this.endpoints == undefined) {
      await this.loadEndpoints().then(data => { this.endpoints = data })
    }
    return this.http.delete(this.endpoints["vsEndpoint"] + this.endpoints["vsgroups"] + "/" + group + "/tenant/" + tenant + "/sla/" + id, { withCredentials: true }).toPromise();
  }

}
