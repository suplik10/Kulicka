package cz.kulicka.services.impl;

import cz.kulicka.constant.BinanceApiConstants;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.security.security.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class BinanceApiServiceGenerator {

    static Logger log = Logger.getLogger(BinanceApiServiceGenerator.class);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
        new Retrofit.Builder()
            .baseUrl(BinanceApiConstants.API_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String apiKey, String secret) {
        if (!StringUtils.isEmpty(apiKey) && !StringUtils.isEmpty(secret)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(apiKey, secret);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                //BinanceApiError apiError = getBinanceApiError(response);
                log.error("api response error> " + response.code() + " message> " + response.message());
                throw new BinanceApiException(response.message());
            }
        } catch (IOException e) {
            log.error("Binance rest call api exception!");
            throw new BinanceApiException(e.getMessage());
        }
    }

    /**
     * Extracts and converts the response error body into an object.

    public static BinanceApiError getBinanceApiError(Response<?> response) throws IOException, BinanceApiException {
        return (BinanceApiError)retrofit.responseBodyConverter(BinanceApiError.class, new Annotation[0])
            .convert(response.errorBody());
    }
     */
}