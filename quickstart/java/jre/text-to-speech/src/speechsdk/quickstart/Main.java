//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.md file in the project root for full license information.
//
// <code>
package speechsdk.quickstart;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Future;
import com.microsoft.cognitiveservices.speech.*;
import com.wl4g.infra.common.io.FileIOUtils;

import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;

/**
 * Quickstart: synthesize speech using the Speech SDK for Java.
 */
public class Main {

    // 注:
    // 文本转语音目前支持标准语音和神经语音。但是，由于神经语音可提供更自然的语音输出，从而提供更好的最终用户体验，
    // 因此我们将于 2024 年 8 月 31 日停用标准语音，并且在此日期之后将不再支持它们。
    // see:https://learn.microsoft.com/en-us/answers/questions/1368891/tts-speech-synthesis-failed
    // see:https://learn.microsoft.com/en-us/answers/questions/530212/retirement-announcement-upgrade-to-text-to-speech
    /**
     * @param args Arguments are ignored in this sample.
     */
    public static void main(String[] args) {

        // Replace below with your own subscription key
        String speechSubscriptionKey = getStringProperty("AZURE_SPEECH_SUBSCRIPTION_KEY");
        // Replace below with your own service region (e.g., "westus").
        String serviceRegion = "westus";

        // Creates an instance of a speech synthesizer using speech configuration with
        // specified
        // subscription key and service region and default speaker as audio output.
        try (SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)) {
            // Set the voice name, refer to https://aka.ms/speech/voices/neural for full
            // list.
            // https://speech.microsoft.com/portal/aeb3cb51905649da9062509ed1f7c8f4/voicegallery
            // en-US-AvaMultilingualNeural
            // zh-CN-XiaoyiNeural
            // zh-CN-XiaomengNeural
            // zh-CN-XiaoqiuNeural
            // zh-CN-YunjianNeural
            // zh-CN-YunhaoNeural
            config.setSpeechSynthesisVoiceName("zh-CN-XiaoqiuNeural");
            try (SpeechSynthesizer synth = new SpeechSynthesizer(config)) {
                assert (config != null);
                assert (synth != null);

                int exitCode = 1;
                System.out.println("Type some text that you want to speak...");
                System.out.print("> ");
                String text = new Scanner(System.in).nextLine();

                Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(text);
                assert (task != null);

                SpeechSynthesisResult result = task.get();
                assert (result != null);

                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized to speaker for text [" + text + "]");
                    exitCode = 0;
                    FileIOUtils.writeFile(new File("/tmp/output.wav"), result.getAudioData(), false);
                } else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails
                            .fromResult(result);
                    System.out.println("CANCELED: Reason=" + cancellation.getReason());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                        System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                        System.out.println("CANCELED: Did you update the subscription info?");
                    }
                }
                System.exit(exitCode);
            }
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.getMessage());

            assert (false);
            System.exit(1);
        }
    }
}
// </code>
