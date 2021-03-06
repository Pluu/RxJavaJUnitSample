package com.pluu.sample.rxjavajunit

import com.pluu.sample.rxjavajunit.utils.RxJavaUncaughtErrorRule
import com.pluu.sample.rxjavajunit.utils.TestHelper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class SampleTest {

    @get:Rule
    val uncaughtRxJavaErrors = RxJavaUncaughtErrorRule()

    private lateinit var sample: Sample

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        RxJavaPlugins.setComputationSchedulerHandler {
            Schedulers.trampoline()
        }

        sample = Sample()
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()

        sample.close()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fail Case when subscribe
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = AssertionError::class)
    fun sample1_fail() {
        Single.just(1)
            .subscribe { value ->
                fail("Result:$value")
            }
    }

    private fun testCase(callback: (Int) -> Unit) {
        Single.just(1)
            .subscribe { value ->
                callback(value)
            }
    }

    @Test(expected = AssertionError::class)
    fun sample_mockito1() {
        val mockCallback: (Int) -> Unit = mock()
        testCase(mockCallback)

        val paramCapture = argumentCaptor<Int>()
        verify(mockCallback).invoke(paramCapture.capture())

        fail("Result:${paramCapture.firstValue}")
    }

    @Test(expected = AssertionError::class)
    fun sample_mockito2() {
        val mockCallback: (Int) -> Unit = mock()
        testCase(mockCallback)

        verify(mockCallback).invoke(eq(0))
    }

    ///////////////////////////////////////////////////////////////////////////
    // EarlyFail Case
    ///////////////////////////////////////////////////////////////////////////

    private fun testCaseEarlyFail(callback: (Int) -> Unit) {
        Single.just(1)
            .map { it / 0 }
            .subscribe { value ->
                callback(value)
            }
    }

    // RxJavaPlugins.setErrorHandler
    @Test
    fun sample_mockito_with_plugin() {
        val errors = TestHelper.trackPluginErrors()

        val mockCallback: (Int) -> Unit = mock()
        testCaseEarlyFail(mockCallback)
        verifyNoInteractions(mockCallback)

        TestHelper.assertUndeliverable(errors, 0, ArithmeticException::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameter 1 Test
    ///////////////////////////////////////////////////////////////////////////

    // value : 1
    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t1_fail() {
        sample.testParam1 { value ->
            assertTrue(value > 100)
        }
    }

    // value : 1
    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t1_success() {
        sample.testParam1 { value ->
            assertTrue(value > 100)
        }

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // value : 1
    // ?????? ???????????? ??????????????? ??????
    // testParam1??? ????????? ????????? ?????????????????? ????????? ????????? ?????????
    @Test(expected = AssertionError::class)
    fun t1_success2() {
        val mockCallback: (Int) -> Unit = mock()
        sample.testParam1(mockCallback)

        val paramCapture = argumentCaptor<Int>()
        verify(mockCallback).invoke(paramCapture.capture())
        assertTrue(paramCapture.firstValue > 100)
    }

    // RxJavaPlugins.setErrorHandler
    @Test
    fun t1_success3() {
        val errors = TestHelper.trackPluginErrors()

        val mockCallback: (Int) -> Unit = mock()
        sample.testParam1EarlyFail(mockCallback)

        verifyNoInteractions(mockCallback)

        TestHelper.assertUndeliverable(errors, 0, ArithmeticException::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameter 2 Test
    ///////////////////////////////////////////////////////////////////////////

    // list : 1..10
    // key : 1
    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t2_fail() {
        sample.testParam2 { list, _ ->
            assertTrue(list.isEmpty())
        }
    }

    // list : 1..10
    // key : 1
    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t2_success() {
        sample.testParam2 { list, _ ->
            assertTrue(list.isEmpty())
        }

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // list : 1..10
    // key : 1
    // ?????? ???????????? ??????????????? ??????
    // testParam2??? ????????? ????????? ?????????????????? ????????? ????????? ?????????
    @Test(expected = AssertionError::class)
    fun t2_success2() {
        val mockCallback: (List<Int>, Int?) -> Unit = mock()
        sample.testParam2(mockCallback)

        val argumentCaptor1 = argumentCaptor<List<Int>>()
        val argumentCaptor2 = argumentCaptor<Int>()
        verify(mockCallback, times(1)).invoke(argumentCaptor1.capture(), argumentCaptor2.capture())

        assertEquals((1..10).toList(), argumentCaptor1.firstValue)
        assertNull(argumentCaptor2.firstValue)
    }

    // list : 1..10
    // key : 1
    // RxJavaPlugins.setErrorHandler
    @Test
    fun t2_success3() {
        val errors = TestHelper.trackPluginErrors()

        val mockCallback: (List<Int>, Int?) -> Unit = mock()
        sample.testParam2EarlyFail(mockCallback)

        verifyNoInteractions(mockCallback)

        TestHelper.assertUndeliverable(errors, 0, ArithmeticException::class.java)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener Param 1 Test
    ///////////////////////////////////////////////////////////////////////////

    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t3_fail() {
        sample.testListenerParam1(object : Sample.SampleListener {
            override fun callback1(data: Int) {
                fail("?????? ?????????1")
            }

            override fun callback2(data: List<Int>, key: Int?) {
                fail("?????? ?????????2")
            }
        })
    }

    // ?????? ???????????? ??????????????? ??????
    @Test(expected = AssertionError::class)
    fun t3_success() {
        sample.testListenerParam1(object : Sample.SampleListener {
            override fun callback1(data: Int) {
                fail("?????? ?????????1")
            }

            override fun callback2(data: List<Int>, key: Int?) {
                fail("?????? ?????????2")
            }
        })

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // ?????? ???????????? ??????????????? ??????
    // testListenerParam1??? ????????? ????????? ?????????????????? ????????? ????????? ?????????
    @Test(expected = AssertionError::class)
    fun t3_success2() {
        val mockCallback: Sample.SampleListener = mock()
        sample.testListenerParam1(mockCallback)

        verify(mockCallback, never()).callback1(any())
        verify(mockCallback, never()).callback2(any(), anyOrNull())
    }

    // RxJavaPlugins.setErrorHandler
    @Test
    fun t3_success3() {
        val errors = TestHelper.trackPluginErrors()

        val mockCallback: Sample.SampleListener = mock()
        sample.testListenerParam1EarlyFail(mockCallback)

        verifyNoInteractions(mockCallback)

        TestHelper.assertUndeliverable(errors, 0, ArithmeticException::class.java)
    }
}