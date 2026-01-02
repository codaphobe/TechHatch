/**
 * Typography System Usage Example
 * 
 * This component demonstrates how to use the global typography system
 * with Tailwind utility classes.
 * 
 * TYPOGRAPHY UTILITIES:
 * 
 * Font Families:
 * - font-body → Plus Jakarta Sans (for body text and UI)
 * - font-heading → Hahmlet (for headings)
 * 
 * Font Sizes:
 * - text-body → 16px
 * - text-subHeading → 20px
 * - text-heading → 24px
 * 
 * Font Weights:
 * - font-bodyNormal → 400 (regular body text)
 * - font-bodyMedium → 500 (medium body text)
 * - font-bodyBold → 700 (bold body text)
 * - font-headingWeight → 700 (for headings)
 * 
 * Line Heights:
 * - leading-body → 1.5
 * - leading-heading → 1.25
 * 
 * REUSABLE PATTERNS:
 * - Body text: font-body text-body font-bodyNormal leading-body
 * - Medium body: font-body text-body font-bodyMedium leading-body
 * - Bold body: font-body text-body font-bodyBold leading-body
 * - Heading: font-heading text-heading font-headingWeight leading-heading
 * - Subheading: font-heading text-subHeading font-normal
 */

const TypographyExample = () => {
  return (
    <div className="p-8 space-y-8 max-w-4xl mx-auto">
      {/* Job Title - Heading */}
      <h1 className="font-heading text-heading font-headingWeight leading-heading">
        Senior Frontend Developer
      </h1>

      {/* Section Title - Subheading */}
      <h2 className="font-heading text-subHeading font-normal">
        Job Description
      </h2>

      {/* Body Text - Regular */}
      <p className="font-body text-body font-bodyNormal leading-body">
        We are looking for an experienced frontend developer to join our team.
        You will be responsible for building user interfaces using React and
        TypeScript. The ideal candidate should have strong problem-solving skills
        and a passion for creating beautiful, accessible web applications.
      </p>

      {/* Body Text - Medium */}
      <p className="font-body text-body font-bodyMedium leading-body">
        This is medium weight body text, useful for emphasizing certain parts
        of the content without making it bold.
      </p>
      
      {/* Body Text - Semibold */}
      <p className="font-body text-body font-bodySemibold leading-body">
        This is semi-bold body text, perfect for highlighting important information
        or call-to-action text.
      </p>

      {/* Body Text - Bold */}
      <p className="font-body text-body font-bodyBold leading-body">
        This is bold body text, perfect for highlighting important information
        or call-to-action text.
      </p>

      {/* Additional Examples */}
      <div className="space-y-4">
        <h3 className="font-heading text-heading font-headingWeight leading-heading">
          Requirements
        </h3>
        
        <ul className="space-y-2">
          <li className="font-body text-body font-bodyNormal leading-body">
            • 3+ years of experience with React
          </li>
          <li className="font-body text-body font-bodyNormal leading-body">
            • Strong TypeScript skills
          </li>
          <li className="font-body text-body font-bodyNormal leading-body">
            • Experience with Tailwind CSS
          </li>
        </ul>
      </div>

      {/* Button Example - Uses heading font */}
      <button className="px-6 py-3 bg-primary text-primary-foreground rounded-lg">
        Apply Now
      </button>
    </div>
  );
};

export default TypographyExample;

