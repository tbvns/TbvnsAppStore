package xyz.tbvns;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> downloadBucketCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> logoBucketCache = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ipAddress = httpRequest.getRemoteAddr();
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.equals("/apps/download")) {
            handleRateLimit(downloadBucketCache, ipAddress, request, response, chain, this::createDownloadBucket);
        } else if (requestURI.equals("/apps/logo")) {
            handleRateLimit(logoBucketCache, ipAddress, request, response, chain, this::createLogoBucket);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void handleRateLimit(
            Map<String, Bucket> bucketCache,
            String ipAddress,
            ServletRequest request,
            ServletResponse response,
            FilterChain chain,
            Function<String, Bucket> bucketCreator
    ) throws IOException, ServletException {
        Bucket bucket = bucketCache.computeIfAbsent(ipAddress, bucketCreator);
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429);
            response.getWriter().write("Too many requests. Please try again later.");
            response.getWriter().flush();
        }
    }

    private Bucket createDownloadBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofDays(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createLogoBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}