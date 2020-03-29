using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class DrawColorFrame : MonoBehaviour {

    [SerializeField] RawImage background;

    // Start is called before the first frame update
    void Start() {
        NuitrackManager.onColorUpdate += DrawColor;
    }

    // Update is called once per frame
    void Update() {
        
    }

    void DrawColor(nuitrack.ColorFrame frame) { 
        background.texture = frame.ToTexture2D();
    }
}
