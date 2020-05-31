using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class LoginData {

    private static string username;
    private static string token;

    public static string Username {
        get {
            return username;
        }
        set {
            username = value;
        }
    }

    public static string Token {
        get {
            return token;
        }
        set {
            token = value;
        }
    }

}
