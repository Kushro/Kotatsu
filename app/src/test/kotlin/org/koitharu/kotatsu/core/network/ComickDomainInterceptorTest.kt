package org.koitharu.kotatsu.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ComickDomainInterceptorTest {

	private lateinit var mockServer: MockWebServer
	private lateinit var interceptor: ComickDomainInterceptor

	@Before
	fun setUp() {
		mockServer = MockWebServer()
		interceptor = ComickDomainInterceptor()
	}

	@After
	fun tearDown() {
		mockServer.shutdown()
	}

	@Test
	fun `should replace comick io with comick so`() {
		// Setup mock server to act as comick.so
		mockServer.enqueue(MockResponse().setResponseCode(200).setBody("success"))
		mockServer.start()

		val client = OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.addInterceptor { chain ->
				// Redirect comick.so requests to our mock server
				val request = chain.request()
				if (request.url.host == "comick.so") {
					val newUrl = request.url.newBuilder()
						.scheme("http")
						.host(mockServer.hostName)
						.port(mockServer.port)
						.build()
					val newRequest = request.newBuilder().url(newUrl).build()
					chain.proceed(newRequest)
				} else {
					chain.proceed(request)
				}
			}
			.build()

		// Make a request to comick.io
		val request = Request.Builder()
			.url("https://comick.io/test")
			.build()

		val response = client.newCall(request).execute()

		// Verify the request was successful (intercepted and redirected)
		assertEquals(200, response.code)
		assertEquals("success", response.body?.string())

		// Verify that our mock server received the request
		val recordedRequest = mockServer.takeRequest()
		assertEquals("/test", recordedRequest.path)
	}

	@Test
	fun `should not modify non-comick io requests`() {
		// Setup mock server
		mockServer.enqueue(MockResponse().setResponseCode(200).setBody("other-site"))
		mockServer.start()

		val client = OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.build()

		// Make a request to another domain
		val request = Request.Builder()
			.url("http://${mockServer.hostName}:${mockServer.port}/test")
			.build()

		val response = client.newCall(request).execute()

		// Verify the request went through unchanged
		assertEquals(200, response.code)
		assertEquals("other-site", response.body?.string())

		// Verify our mock server received the request
		val recordedRequest = mockServer.takeRequest()
		assertEquals("/test", recordedRequest.path)
	}

	@Test
	fun `should preserve path and query parameters when replacing domain`() {
		// Setup mock server to act as comick.so
		mockServer.enqueue(MockResponse().setResponseCode(200).setBody("success"))
		mockServer.start()

		val client = OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.addInterceptor { chain ->
				// Redirect comick.so requests to our mock server
				val request = chain.request()
				if (request.url.host == "comick.so") {
					val newUrl = request.url.newBuilder()
						.scheme("http")
						.host(mockServer.hostName)
						.port(mockServer.port)
						.build()
					val newRequest = request.newBuilder().url(newUrl).build()
					chain.proceed(newRequest)
				} else {
					chain.proceed(request)
				}
			}
			.build()

		// Make a request to comick.io with path and query parameters
		val request = Request.Builder()
			.url("https://comick.io/manga/test?page=1&sort=title")
			.build()

		val response = client.newCall(request).execute()

		// Verify the request was successful
		assertEquals(200, response.code)

		// Verify that path and query parameters were preserved
		val recordedRequest = mockServer.takeRequest()
		assertEquals("/manga/test?page=1&sort=title", recordedRequest.path)
	}
}