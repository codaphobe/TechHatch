import { AlertCircle, Loader2 } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

/**
 * ErrorState component for displaying error messages
 * @param {Object} props
 * @param {string} props.title - Error title
 * @param {string} props.description - Error description
 * @param {Function} [props.onRetry] - Optional retry callback function
 * @param {boolean} [props.retrying=false] - Whether retry is in progress
 * @param {string} [props.className] - Optional additional CSS classes
 */
function ErrorState({
  title,
  description,
  onRetry,
  retrying = false,
  className,
}) {
  return (
    <Card className={cn("w-full max-w-md mx-auto", className)}>
      <CardHeader className="text-center">
        <div className="flex justify-center mb-4">
          <AlertCircle className="h-12 w-12 text-destructive" />
        </div>
        <CardTitle className="text-xl">{title}</CardTitle>
        <CardDescription className="text-base mt-2">{description}</CardDescription>
      </CardHeader>
      {onRetry && (
        <CardContent className="flex justify-center">
          <Button
            onClick={onRetry}
            disabled={retrying}
            variant="outline"
            className="min-w-[120px]"
          >
            {retrying ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Retrying...
              </>
            ) : (
              "Try Again"
            )}
          </Button>
        </CardContent>
      )}
    </Card>
  );
}

export default ErrorState;

