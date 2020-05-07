from flask import Flask, request, make_response, jsonify
from flask_cors import CORS, cross_origin
import json
from secrets import token_urlsafe

app = Flask(__name__)
cors = CORS(app, support_credentials=True)


# new tenant endpoint: /portal/catalogue/vsblueprint
#new: remoteTenantInfos
groups = [{
    "name": "user",
    "tenants": [{
        "remoteTenantInfos": [{
            "host": "string",
            "remoteTenantName": "string",
            "remoteTenantPwd": "string"
        }],
        "username": "teste",
        "password": "teste",
        "sla": [{
            "id": 1,
            "slaConstraints": [{
                "scope": "GLOBAL_VIRTUAL_RESOURCE",
                "maxResourceLimit": {
                    "diskStorage": 100,
                    "vCPU": 100,
                    "memoryRAM": 100
                },
                "location": "teste"
            }],
            "slaStatus": "ENABLED"
        }],
        "vsdId": [1],
        "vsiId":[1],
        "allocatedResources":{"diskStorage": 100, "vCPU": 100, "memoryRAM": 100}
    }, {
        "username": "user",
        "password": "user",
        "sla": [{
            "slaConstraints": {
                "scope": "GLOBAL_VIRTUAL_RESOURCE",
                "maxResourceLimit": {
                    "diskStorage": 100,
                    "vCPU": 100,
                    "memoryRAM": 100
                },
                "location": "teste"
            },
            "slaStatus": "ENABLE"
        }],
        "vsdId": [1],
        "vsiId": [1],
        "allocatedResources": {
            "diskStorage": 100,
            "vCPU": 100,
            "memoryRAM": 100
        },
    }]
}, {
    "name": "admin",
    "tenants": [{
        "username": "admin",
        "password": "admin",
        "sla": [{
            "slaConstraints": {
                "scope": "GLOBAL_VIRTUAL_RESOURCE",
                "maxResourceLimit": {
                    "diskStorage": 100,
                    "vCPU": 100,
                    "memoryRAM": 100
                },
                "location": "teste"
            },
            "slaStatus": "ENABLED"
        }],
        "vsdId": [1],
        "vsiId": [1],
        "allocatedResources": {"diskStorage": 100, "vCPU": 100, "memoryRAM": 100},
    }]
}]


# new blueprint endpoint: /portal/catalogue/vsblueprint
#new: applicationMetrics | compatibleContextBlueprint | compatibleSites
#changed: serviceSequence
blueprints = [{
    "vsBlueprintId": 1,
    "vsBlueprintVersion": "teste",
    "name": "teste",
    "vsBlueprint": {
        "applicationMetrics": [{
            "interval": "string",
            "metricCollectionType": "CUMULATIVE",
            "metricId": "string",
            "name": "string",
            "topic": "string",
            "unit": "string"
        }],
        "compatibleContextBlueprint": [
            "string"
        ],
        "compatibleSites": [
            "ITALY_TURIN"
        ],
        "vsBlueprintId": 1,
        "version": "teste",
        "name": "teste",
        "description": "teste",
        "imgUrl": "teste",
        "parameters": [{
            "parameterId": "teste",
            "parameterName": "teste",
            "parameterType": "teste",
            "parameterDescription": "teste",
            "applicabilityField": "teste"
        }],
        "atomicComponents": [{
            "componentId": 1,
            "serversNumber": 1,
            "imagesUrls": ["teste1", "teste2"],
            "endPointsIds":["teste1", "teste2"],
            "lifecycleOperations":{"teste": "teste"}
        }],
        "serviceSequence": [{
            "hopEndPoints": [{
                "endPointId": "string",
                "vsComponentId": "string"
            }]
        }],
        "endPoints": [{
            "endPointId": "teste",
            "external": True,
            "management": True,
            "ranConnection": True
        }],
        "connectivityServices": [{
            "endPointIds": [1],
            "external":True,
            "connectivityProperties":["teste1", "teste2"]
        }],
        "configurableParameters": ["teste1", "teste2"]
    },
    "onBoardedNsdInfoId": [1],
    "onBoardedVnfPackageInfoId": [1],
    "onBoardedMecAppPackageInfoId": [1],
    "activeVsdId": [1]
}]

cookies = {}


@app.route('/login', methods=['POST'])
@cross_origin(supports_credentials=True)
def login():
    if request.form.get("username") in [y["username"] for x in groups for y in x["tenants"] if x["name"] == "admin"]:
        resp = make_response("")
        token = token_urlsafe(64)
        cookies[token] = request.form.get("username")
        resp.set_cookie('JSESSION', token)
    else:
        resp = make_response("")
        token = token_urlsafe(64)
        cookies[token] = request.form.get("username")
        resp.set_cookie('JSESSION', token)
    return resp


@app.route('/vs/whoami')
@cross_origin(supports_credentials=True)
def whoami():
    username = cookies[request.cookies.get("JSESSION")]

    for group in groups:
        for tenant in group["tenants"]:
            if tenant["username"] == username:
                t = tenant.copy()
                t["role"] = "TENANT" if group["name"] != "admin" else "ADMIN"
                return t


# -----------------Groups and Tenants------------------


@app.route('/vs/admin/group')
@cross_origin(supports_credentials=True)
def getGroups():
    return jsonify(groups)


@app.route('/vs/admin/groups/tenants')
@cross_origin(supports_credentials=True)
def getTenants():
    tenants = [y for x in groups for y in x["tenants"]]
    return jsonify(tenants)


@app.route('/vs/admin/group/<groupName>/tenant')
@cross_origin(supports_credentials=True)
def getGroupTenants(groupName):
    tenants = [x["tenants"] for x in groups if x["name"] == groupName][0]
    return jsonify(tenants)


@app.route("/vs/admin/group/<groupName>", methods=["POST"])
@cross_origin(supports_credentials=True)
def insertGroup(groupName):
    existing = [x["name"] for x in groups]
    if groupName in existing:
        return "", 400
    groups.append({"name": groupName, "tenants": []})
    return ""


@app.route("/vs/admin/group/<groupName>", methods=["DELETE"])
@cross_origin(supports_credentials=True)
def deleteGroup(groupName):
    group = [x for x in groups if x["name"] == groupName][0]
    groups.remove(group)
    return ""


@app.route("/vs/admin/group/<groupName>/tenant", methods=["POST"])
@cross_origin(supports_credentials=True)
def insertTenantInGroup(groupName):
    data = json.loads(request.data)
    data["allocatedResources"] = {"diskStorage": 0, "vCPU": 0, "memoryRAM": 0}
    data["sla"] = []
    group = [x for x in groups if x["name"] == groupName][0]
    group["tenants"].append(data)
    return ""


@app.route("/vs/admin/group/<groupName>/tenant/<tenantName>", methods=["DELETE"])
@cross_origin(supports_credentials=True)
def deleteTenantInGroup(groupName, tenantName):
    group = [x for x in groups if x["name"] == groupName][0]
    tenant = [x for x in group["tenants"] if x["username"] == tenantName][0]
    group["tenants"].remove(tenant)
    return ""


@app.route("/vs/admin/group/<groupName>/tenant/<tenantName>/sla", methods=["POST"])
@cross_origin(supports_credentials=True)
def createSLA(groupName, tenantName):
    sla = json.loads(request.data)
    group = [x for x in groups if x["name"] == groupName][0]
    tenant = [x for x in group["tenants"] if x["username"] == tenantName][0]
    del sla["tenant"]
    sla["id"] = tenant["sla"][-1]["id"]+1 if len(tenant["sla"]) > 0 else 1
    tenant["sla"].append(sla)
    return ""


@app.route("/vs/admin/group/<groupName>/tenant/<tenantName>/sla")
@cross_origin(supports_credentials=True)
def getSLA(groupName, tenantName):
    group = [x for x in groups if x["name"] == groupName][0]
    tenant = [x for x in group["tenants"] if x["username"] == tenantName][0]
    return jsonify(tenant["sla"])


@app.route("/vs/admin/group/<groupName>/tenant/<tenantName>/sla/<slaId>", methods=["DELETE"])
@cross_origin(supports_credentials=True)
def deleteSLA(groupName, tenantName, slaId):
    group = [x for x in groups if x["name"] == groupName][0]
    tenant = [x for x in group["tenants"] if x["username"] == tenantName][0]
    sla = [x for x in tenant["sla"] if x["id"] == int(slaId)][0]
    tenant["sla"].remove(sla)
    return ""

# --------------Blueprints----------------


@app.route('/vs/catalogue/vsblueprint')
@cross_origin(supports_credentials=True)
def blueprint():
    return jsonify(blueprints)


@app.route("/vs/catalogue/vsblueprint", methods=["POST"])
@cross_origin(supports_credentials=True)
def addBlueprint():
    data = json.loads(request.data)
    blueprint = data["vsBlueprint"]
    out = {"vsBlueprintId": blueprints[-1]["vsBlueprintId"]+1, "name": blueprint["name"],
           "vsBlueprintVersion": blueprint["version"], "vsBlueprint": blueprint}
    out["onBoardedNsdInfoId"] = []
    out["onBoardedVnfPackageInfoId"] = []
    out["onBoardedMecAppPackageInfoId"] = []
    out["activeVsdId"] = []
    blueprints.append(out)
    return ""


@app.route("/vs/catalogue/vsblueprint/<blueprintId>", methods=["DELETE"])
@cross_origin(supports_credentials=True)
def deleteBlueprint(blueprintId):
    blueprintId = int(blueprintId)
    blueprint = [x for x in blueprints if x["vsBlueprintId"]
                 == blueprintId][0]
    blueprints.remove(blueprint)
    return ""


@app.route("/vs/catalogue/vsblueprint/<blueprintId>")
@cross_origin(supports_credentials=True)
def getBlueprint(blueprintId):
    blueprintId = int(blueprintId)
    blueprint = [x for x in blueprints if x["vsBlueprintId"] == blueprintId][0]
    return jsonify(blueprint)


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8082)
