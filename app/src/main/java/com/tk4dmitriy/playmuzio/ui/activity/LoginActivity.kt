package com.tk4dmitriy.playmuzio.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.TAG

private const val CLIENT_ID = "de2e8294ede44ef0a1e4bc26e4a094e6"
private const val REDIRECT_URI = "com.tk4dmitriy.playmuzio://callback"
private const val AUTH_CODE = 100

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authorization()
    }

    private fun authorization() {
        val builder: AuthorizationRequest.Builder = AuthorizationRequest.Builder(CLIENT_ID,
            AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        val scopes: Array<String> = arrayOf("streaming", "user-read-private", "user-top-read", "user-read-playback-state")
        builder.setScopes(scopes)
        val request: AuthorizationRequest = builder.build()

        AuthorizationClient.openLoginActivity(this, AUTH_CODE, request)
    }

    @Deprecated(message = "Features of Spotify Api only allow onActivityResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_CODE) {
            val response: AuthorizationResponse = AuthorizationClient.getResponse(resultCode, data)
            val typeResponse = response.type

            typeResponse?.let {
                when(typeResponse) {
                    AuthorizationResponse.Type.TOKEN -> processingToken(response.accessToken)
                    AuthorizationResponse.Type.ERROR -> processingError(response.error)
                    AuthorizationResponse.Type.CODE -> processingCode(response.code)
                    AuthorizationResponse.Type.EMPTY -> processingEmpty()
                    AuthorizationResponse.Type.UNKNOWN -> processingUnknown()
                }
            }
        }
    }

    private fun processingToken(token: String) {
        Constants.TOKEN = token
        Log.d(TAG, Constants.TOKEN)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun processingError(error: String) {
        Log.d(TAG, "Error: $error")
        Toast.makeText(this, resources.getString(R.string.error_authorization), Toast.LENGTH_SHORT).show()
        finishAffinity()
    }

    private fun processingCode(code: String) {
        Log.d(TAG, "Code: $code")
        Toast.makeText(this, resources.getString(R.string.error_authorization), Toast.LENGTH_SHORT).show()
        finishAffinity()
    }

    private fun processingEmpty() {
        Log.d(TAG, "Empty")
        Toast.makeText(this, resources.getString(R.string.error_authorization), Toast.LENGTH_SHORT).show()
        finishAffinity()
    }

    private fun processingUnknown() {
        Log.d(TAG, "UNKNOWN")
        Toast.makeText(this, resources.getString(R.string.error_authorization), Toast.LENGTH_SHORT).show()
        finishAffinity()
    }
}