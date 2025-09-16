package org.koitharu.kotatsu.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that replaces comick.io domain with comick.so in HTTP requests
 */
class ComickDomainInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val originalRequest = chain.request()
		val originalUrl = originalRequest.url
		
		// Check if the host is comick.io and replace with comick.so
		if (originalUrl.host == "comick.io") {
			val newUrl = originalUrl.newBuilder()
				.host("comick.so")
				.build()
				
			val newRequest = originalRequest.newBuilder()
				.url(newUrl)
				.build()
				
			return chain.proceed(newRequest)
		}
		
		return chain.proceed(originalRequest)
	}
}