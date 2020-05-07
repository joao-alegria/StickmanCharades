import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  keyTranslations = {
    allocatedResources: "Allocated Resources",
    diskStorage: "Disk Storage Space",
    memoryRAM: "RAM Capacity",
    vCPU: "Number of CPUs",
    password: "Password",
    sla: "Service Level Agreement",
    id: "Identifier",
    slaConstraints: "Service Level Agreement Defined Constraints",
    location: "Location",
    maxResourceLimit: "Service Level Agreement Maximum Resource Limitation",
    scope: "Scope",
    slaStatus: "Service Level Agreement Status",
    username: "Username",
    vsdId: "Vertical Slice Descriptor Identifiers",
    vsiId: "Vertical Slice Instances Identifiers"
  }

  constructor() { }

  translate(key) {
    if (key in this.keyTranslations) {
      return this.keyTranslations[key]
    }
    return key
  }
}
