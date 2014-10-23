/**
 * Created by Dustin on 5/20/14.
 */
function initializeRegion(region, teams) {
    for (var i = 0; i <= 15; i++) {
        var seed = i + 1;
        region.find("span.seed:contains('" + seed + "')")
            .filter(function(index) { return $(this).text() === seed.toString(); })
            .after(teams[i]);
    }
}