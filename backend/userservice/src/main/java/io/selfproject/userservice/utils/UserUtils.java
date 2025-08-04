package io.selfproject.userservice.utils;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import io.selfproject.userservice.exception.ApiException;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static io.selfproject.userservice.constant.Constants.RIAHI_S_LLS;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class UserUtils {

    public static Supplier<String> randomUUID = () -> UUID.randomUUID().toString();

    public static Supplier<String> memberId = () -> randomNumeric(4) + "-" + randomNumeric(2) + "-" + randomNumeric(4);

    public static Function<String, QrData> qrDataFunction = qrCodeSecret -> new QrData.Builder()
            .issuer(RIAHI_S_LLS)
            .label(RIAHI_S_LLS)
            .secret(qrCodeSecret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

    public static Function<String, String> qrCodeImageUri = qrCodeSecret -> {
    try{
        var data = qrDataFunction.apply(qrCodeSecret);
        var generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(data);
        return getDataUriForImage(imageData, generator.getImageMimeType());
    }catch (Exception exception) {
        throw new ApiException(exception.getMessage());
    }
    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();





}
