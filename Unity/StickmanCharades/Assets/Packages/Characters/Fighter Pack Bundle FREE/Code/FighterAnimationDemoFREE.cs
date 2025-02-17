﻿using UnityEngine;
using System.Collections;

public class FighterAnimationDemoFREE : MonoBehaviour {
	
	public Animator animator;

	private Transform defaultCamTransform;
	private Vector3 resetPos;
	private Quaternion resetRot;
	private GameObject cam;
	private GameObject fighter;

	private Vector3 posModifier = new Vector3(-4.11f,-0.18f,-4.15f);
	private Vector3 posModifierPreview = new Vector3(-1535.0f,-0.18f,-1287.0f);

	void Start()
	{
		cam = GameObject.FindWithTag("MainCamera");
		defaultCamTransform = cam.transform;
		resetPos = defaultCamTransform.position;
		resetRot = defaultCamTransform.rotation;
		fighter = GameObject.FindWithTag("Player");
		fighter.transform.position = posModifier + posModifierPreview; //new Vector3(0,0,0);
	}
	/*
	void OnGUI () 
	{
		if (GUI.RepeatButton (new Rect (815, 535, 100, 30), "Reset Scene")) 
		{
			defaultCamTransform.position = resetPos;
			defaultCamTransform.rotation = resetRot;
			fighter.transform.position = posModifier + posModifierPreview; //new Vector3(0,0,0);
			animator.Play("Idle");
		}

		if (GUI.RepeatButton (new Rect (25, 20, 100, 30), "Walk Forward")) 
		{
			animator.SetBool("Walk Forward", true);
		}
		else
		{
			animator.SetBool("Walk Forward", false);
		}

		if (GUI.RepeatButton (new Rect (25, 50, 100, 30), "Walk Backward")) 
		{
			animator.SetBool("Walk Backward", true);
		}
		else
		{
			animator.SetBool("Walk Backward", false);
		}

		if (GUI.Button (new Rect (25, 90, 100, 30), "Punch")) 
		{
			animator.SetTrigger("PunchTrigger");
		}
	}
	*/
}