## Questions & Assumptions

- Is Motorbike a typo? From a short google search found that Motorbike and Motorcycle can be used interchangeably, so I decided to rename the Motorbike class to Motorcycle since the latter was mentioned in the business requirements;
- How many days before a public holiday need to be considered for the charge free day? The business requirement is ambiguous `...days before a public holiday`. I assumed to be one day before;
- What happens if a public holiday is during the weekend? Does the day before rule still apply? I assumed it does;
- I found multiple variants of public holidays for Sweden and didn't know exactly which one to use. I decided to use the list from here https://www.calendarlabs.com/holidays/sweden/2013 as the source of truth;
- Is the `getTax` method suppose to receive multiple timeframes from the same day or multiple timeframes from multiple days? I assumed it can receive timeframes from multiple days;
- When applying the single charge rule, how do we calculate the 60 minutes window? Is it 60 minutes from the first occurrence in the interval or just a fixed hour interval (9:00-9:59, 10:00-10:59, etc). I assumed the 60 minutes since the first timestamp in the interval; 
- How do we apply the single charge rule for toll dates that are not ordered? I assumed the input dates should be ordered, so I explicitly sorted them before calling the calculateTax;
- Should the generic version work for multiple countries as well as multiple cities? I assumed the generic solution should accommodate only multiple cities in the same country (Sweden);
- I assumed the post-it dates should be used for an acceptance test (returnCongestionTax_whenTollDatesMatchMultipleRules);
---
## Feel free to document what you wanted to focus on in the time given?
 - Main focus was on refactoring the code to be readable, easier to understand and to reflect the ubiquitous language used in the business requirements, while running the code against a comprehensive suite of automated tests to make sure it behaves as expected. The initial code was horrendously written and had no tests, hence the several bugs;
 - I left a specific version of the CongestionTaxCalculator for the Gothenburg requirements `GothenburgCongestionTaxCalculator` just to showcase how the code would have looked before evolving to a more generic design;
 - For the bonus scenario I chose to store the rules inside a yml file `classpath:/rules/congestion-tax-rules.yml` which is loaded at runtime inside CongestionTaxRuleContainer;
---
## Additional work you would like to have done with more time?
- Charge free days rules were not extracted in the `resources/rules/congestion-tax-rules.yml` because of time constraints. Those would be the hardest to do since we would need a separate list of chargeFreeDays depending on the year and city. Also, it is not clear which of the rules would vary from city to city - it would require a discussion with the domain experts. Probably those would either come from a separate service API when querying by year and city, or be computed in-flight and cached in the congestion-tax-calculator service. 
For the second solution we would still need an external service that would provide a list of public holidays by year + some extra values in the rules YML. Maybe something like this:
```
congestion-tax-rules-by-city:
  gothenburg:
    charge-free-days:
        include-weekends: true
        include-months:
            - JULY
        include-days-before-public-holidays: true
```
- Cache PublicHolidaysProvider + use a real implementation for the Calendar client like an external service;
- Not thrilled with groupTollDatesInSingleChargeWindows method. Would have liked to write it using functional style, but didn't have time to think of a better solution;
- Write tests for other city configurations;
- Write more acceptance tests with help from the business;
- OpenAPI definition;
- Improve error handling with some specific domain exceptions;
- Improve responses for error scenarios. Now using the default messages from Spring;
- For the bonus scenario I decided to externalize the rules using a yml file. I thought it would be enough to demonstrate the behaviour in the time constraint. The implementation can be easily swapped to fetch the rules from a DB or external service by providing a different implementation to the CongestionTaxRulesProvider interface;
- Architecture tests (ex: using archunit or spring modulith) that would validate the packaging structure (ex: domain is independent from adapters, domain objects are not leaked via controllers, etc);
